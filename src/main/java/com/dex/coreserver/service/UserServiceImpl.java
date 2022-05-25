package com.dex.coreserver.service;

import com.dex.coreserver.dto.UserPasswordChangingDTO;
import com.dex.coreserver.exceptions.AppDataIntegrityViolationException;
import com.dex.coreserver.exceptions.AppException;
import com.dex.coreserver.model.Role;
import com.dex.coreserver.model.User;
import com.dex.coreserver.model.enums.Actions;
import com.dex.coreserver.payload.LoginRequest;
import com.dex.coreserver.repository.UserRepository;
import com.dex.coreserver.security.JwtTokenProvider;
import com.dex.coreserver.util.ApplicationUtils;
import com.dex.coreserver.util.CyrillicLatinConverter;
import com.dex.coreserver.util.DescriptionUtils;
import com.dex.coreserver.util.SecurityUtils;
import com.dex.coreserver.util.Validate;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.exceptions.QrGenerationException;
import dev.samstevens.totp.qr.QrData;
import dev.samstevens.totp.qr.QrDataFactory;
import dev.samstevens.totp.qr.QrGenerator;
import dev.samstevens.totp.secret.SecretGenerator;
import org.apache.commons.lang.time.DateUtils;
import org.passay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.text.Normalizer;
import java.util.List;
import java.util.Map;

import static dev.samstevens.totp.util.Utils.getDataUriForImage;

@Service
public class UserServiceImpl extends BasicServiceImpl<User> implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private QrDataFactory qrDataFactory;
    @Autowired
    private QrGenerator qrGenerator;
    @Autowired
    private SecretGenerator secretGenerator;
    @Autowired
    private CodeVerifier verifier;
    @Autowired
    private JwtTokenProvider tokenProvider;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private RoleService roleService;

    @Value("${passwordSpecialCharacters}")
    private String passwordSpecialCharacters;

    @Value("${username.generator}")
    private boolean usernameGeneratorActive;

    @Value("${password.expiration.months}")
    private Integer passwordExpirationMonths;

    @Override
    protected JpaRepository<User, Long> getRepository() {
        return userRepository;
    }

    @Override
    protected Actions getCreateAction() {
        return Actions.USER_CREATE;
    }

    @Override
    protected Actions getDeleteAction() {
        return Actions.USER_DELETE;
    }

    @Override
    protected Actions getUpdateAction() {
        return Actions.USER_UPDATE;
    }

    @Override
    protected Actions getFindAllAction() {
        return Actions.USER_FIND_ALL;
    }

    @Override
    protected void validate(User entity, User user) throws AppException {
        Validate.isNotNull(entity, "USER_IS_NULL");
    }

    @Override
    protected void validatePersistenceException(DataAccessException e) throws AppException, PersistenceException {
        if (e instanceof DataIntegrityViolationException) {
            if (e.getMostSpecificCause().getMessage().indexOf("unq_username") > 0) {
                throw new AppDataIntegrityViolationException("USER_WITH_TYPED_USERNAME_ALREADY_EXIST");
            }
        } else {
            throw new AppException("UNKNOWN_EXCEPTION", e);
        }
    }

    @Override
    public User create(User userForCreation, String username) throws AppException {
        Validate.isNotNull(userForCreation, "USER_IS_NULL");
        Validate.isNotNull(userForCreation.getUsername(), "USERNAME_IS_NULL");
        Validate.isNotNull(userForCreation.getPassword(), "PASSWORD_IS_NULL");
        if(!isUsernameGeneratorActive()){
            Validate.isNotNull(userForCreation.getConfirmPassword(), "CONFIRM_PASSWORD_IS_NULL");
            Validate.isEquals(userForCreation.getPassword(), userForCreation.getConfirmPassword(),
                "PASSWORD_AND_CONFIRM_PASSWORD_INEQUALITY");
        }else{
            userForCreation.setPasswordExpirationDate(DateUtils.addMonths(new Date(), passwordExpirationMonths));
        }
        userForCreation.setUsername(userForCreation.getUsername().toLowerCase());
        userForCreation.setPassword(bCryptPasswordEncoder.encode(userForCreation.getPassword()));
        return super.create(userForCreation, username);
    }

    @Override
    public User update(User userForUpdate, String username) {
        Validate.isEntityOrIdNotNull(userForUpdate, "USER_OR_USER_ID_IS_NULL");
        User user = findById(userForUpdate.getId());
        Validate.isNotNull(userForUpdate.getUsername(), "USERNAME_IS_NULL");
        Validate.isNotNull(user.getPassword(), "PASSWORD_IS_NULL");
        userForUpdate.setPassword(user.getPassword());
        userForUpdate.setUsername(userForUpdate.getUsername().toLowerCase());
        return super.update(userForUpdate, username);
    }

    @Override
    public User updatePassword(User user, String username) {
        Validate.isEntityOrIdNotNull(user, "USER_OR_USER_ID_IS_NULL");
        Validate.isNotNull(user.getPassword(), "PASSWORD_IS_NULL");
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return super.update(user, username);
    }

    @Transactional
    @Override
    public User deactivateUser(Long id, String username) {
        User userForDeactivation = findById(id);
        userForDeactivation.setActive(false);
        return update(userForDeactivation, username);
    }

    @Transactional
    @Override
    public User activateUser(Long id, String username) {
        User userForDeactivation = findById(id);
        userForDeactivation.setActive(true);
        return update(userForDeactivation, username);
    }

    @Transactional
    @Override
    public User deactivate2fa(Long id, String username) {
        User userFor2faDeactivation = findById(id);
        userFor2faDeactivation.setUseGoogle2f(false);
        userFor2faDeactivation.setGoogle2FaSecret(null);
        return update(userFor2faDeactivation, username);
    }

    @Transactional
    @Override
    public String register2fa(User loggedUser, Long id) {
        User userForRegistration = findById(id);
        userForRegistration.setGoogle2FaSecret(secretGenerator.generate());
        try {
            QrData data = qrDataFactory.newBuilder().label(userForRegistration.getUsername())
                    .secret(userForRegistration.getGoogle2FaSecret()).issuer(DescriptionUtils.getAppName()).build();
            String qrCodeImage = getDataUriForImage(qrGenerator.generate(data), qrGenerator.getImageMimeType());
            update(userForRegistration, loggedUser.getUsername());
            return qrCodeImage;
        } catch (QrGenerationException e) {
            throw new AppException("QR_GENERATION_EXCEPTION_OCCURRED");
        }
    }

    @Transactional
    @Override
    public User confirm2fa(String verifyCode, User loggedUser, Long id) {
        User userForConfirmation = findById(id);
        boolean isValidCode = verifier.isValidCode(userForConfirmation.getGoogle2FaSecret(), verifyCode);
        if (!isValidCode) {
            return null;
        }
        userForConfirmation.setUseGoogle2f(true);
        return update(userForConfirmation, loggedUser.getUsername());
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Map<String, Object> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate( new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    ) );
        } catch (BadCredentialsException e){
            throw new AppException( "WRONG_USERNAME_OR_PASSWORD" );
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User authenticatedUser = (User) authentication.getPrincipal();
        Map<String, Object> response = new HashMap<>();
        response.put("accessToken", SecurityUtils.getTokenPrefix() + tokenProvider.generateAccessToken(authentication));
        response.put("refreshToken", tokenProvider.generateRefreshToken(authentication));
        if(isUsernameGeneratorActive()){
            boolean shouldChangePassword = shouldChangePassword(authenticatedUser, loginRequest.getPassword());
            response.put("shouldChangePassword", shouldChangePassword);
        }else{
            response.put("shouldChangePassword", false);
        }
        if (!(Boolean)response.get( "shouldChangePassword" )) setLastLoginDate(authenticatedUser);
        return response;
    }

    @Override
    public List<Actions> getAllowedActions(String username, Long id) {
        User readUser = readUser(username);
        List<Actions> allowedActions = readUser.myActions();
        allowedActions = ApplicationUtils.filterActionsList(allowedActions);
        return allowedActions;
    }

    @Override
    public List<User> findAll(String username){
        return userRepository.findAll();
    }

    @Override
    public String generateUsername(String firstName, String lastName, String principalName) {
        readUser(principalName);
        String firstNameTrimmed = firstName.trim();
        String lastNameTrimmed = lastName.trim();
        validateFirstNameAndLastName(firstNameTrimmed, lastNameTrimmed);
        String firstNameInLatin = CyrillicLatinConverter.checkIfStringIsInCyrillicAndConvertToLatin(firstNameTrimmed);
        String lastNameInLatin = CyrillicLatinConverter.checkIfStringIsInCyrillicAndConvertToLatin(lastNameTrimmed);
        String firstNameEnglishAlphabet = convertStringToLowerCaseEnglishAlphabet(firstNameInLatin);
        String lastNameEnglishAlphabet = convertStringToLowerCaseEnglishAlphabet(lastNameInLatin);
        String username = null;
        for (int i = 1; i <= firstNameEnglishAlphabet.length(); i++) {
            String testUsername = firstNameEnglishAlphabet.substring(0, i).concat(lastNameEnglishAlphabet);
            userRepository.existsByUsername(testUsername);
            if (!userRepository.existsByUsername(testUsername)) {
                username = testUsername;
                break;
            }
        }
        if (username == null) {
            int counter = 1;
            while (username == null) {
                String testUsername = firstNameEnglishAlphabet.concat(String.valueOf(counter++)).concat(lastNameEnglishAlphabet);
                if (!userRepository.existsByUsername(testUsername)) {
                    username = testUsername;
                }
            }
        }
        return username;
    }

    @Override
    public boolean isUsernameGeneratorActive() {
        return usernameGeneratorActive;
    }

    private void validateFirstNameAndLastName(String firstName, String lastName) {
        if (!containsOnlyLetters(firstName))
            throw new AppException("FIRST_NAME_IS_NOT_VALID");
        if (!containsOnlyLetters(lastName))
            throw new AppException("LAST_NAME_IS_NOT_VALID");
    }

    private boolean containsOnlyLetters(String input) {
        return input.chars().allMatch(Character::isLetter);
    }

    private String convertStringToLowerCaseEnglishAlphabet(String text) {
        String nfdNormalizedString = Normalizer.normalize(text.toLowerCase(), Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString)
                .replaceAll("")
                .replaceAll("đ", "dj")
                .replaceAll("ž", "z")
                .replaceAll("ć", "c")
                .replaceAll("č", "c")
                .replaceAll("š", "s")
                .replaceAll(" ", "");
    }

    @Override
    public Map<String, String> getAppInfo() {
        Map<String, String> response = new HashMap<>();
        response.put("appName", DescriptionUtils.getAppName());
        response.put("appLocale", DescriptionUtils.getLocale());
        response.put("appVersion", DescriptionUtils.getAppVersion());
        return response;
    }


    @Override
    public String generateRandomPassword(String username, int... passwordLength) {
        readUser(username);
        int passLength = passwordLength.length > 0 ? passwordLength[0] : 0;
        if (passLength < 8) passLength = 8;

        PasswordGenerator generator = new PasswordGenerator();
        CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);

        CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);

        CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);

        CharacterData specialChars = new CharacterData() {
            public String getErrorCode() {
                return CharacterCharacteristicsRule.ERROR_CODE;
            }

            public String getCharacters() {
                return passwordSpecialCharacters;
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);

        return generator.generatePassword(passLength, splCharRule, lowerCaseRule, upperCaseRule, digitRule);
    }

    @Override
    public User changePassword(User userForPassChange, String username) {
        readUser(username);
        Validate.isEntityOrIdNotNull(userForPassChange, "USER_OR_USER_ID_IS_NULL");
        User userForUpdate = findById(userForPassChange.getId());
        Validate.isNotNull(userForUpdate, "USER_YOU_WANT_TO_UPDATE_DOES_NOT_EXIST_IN_DATABASE");
        Validate.isNotNull(userForUpdate.getRoles(), "USER_ROLES_ARE_NULL");
        Role mostPriorityRoleForUser = findTopPriorityRoleByUser(userForUpdate);
        Validate.isNotNull(userForPassChange.getPassword(), "PASSWORD_IS_NULL");
        Validate.isNotNull(userForPassChange.getConfirmPassword(), "CONFIRM_PASSWORD_IS_NULL");
        Validate.isEquals(userForPassChange.getPassword(), userForPassChange.getConfirmPassword(),
                "PASSWORD_AND_CONFIRM_PASSWORD_INEQUALITY");
        Validate.isNotNull(mostPriorityRoleForUser.getRegex(), "ROLE_REGEX_IS_NULL");
        checkIfNewPasswordEqualCurrentPassword(userForPassChange.getPassword(), userForUpdate.getPassword());
        boolean passwordMatchesRegex = passwordRegExMatcher(userForPassChange.getPassword(), mostPriorityRoleForUser.getRegex());
        if (!passwordMatchesRegex) {
            throw new AppException("PASSWORD_DOES_NOT_MATCH_APPROPRIATE_REGEX", mostPriorityRoleForUser.getRegexDescription());
        }
        userForUpdate.setPassword(bCryptPasswordEncoder.encode(userForPassChange.getPassword()));
        return super.update(userForUpdate, username);
    }

    @Override
    public boolean shouldChangePassword(User user, String password) {
        Validate.isNotNull(user.getRoles(), "USER_ROLES_ARE_NULL");
        Role userRole = findTopPriorityRoleByUser(user);
        return (isFirstLogin(user) || !passwordRegExMatcher(password, userRole.getRegex()) || user.isPasswordExpired());
    }

    private void checkIfNewPasswordEqualCurrentPassword(String newPassword, String oldPassword) {
        if (bCryptPasswordEncoder.matches(newPassword, oldPassword)) {
            throw new AppException("NEW_PASSWORD_IS_THE_SAME_AS_OLD_PASSWORD");
        }
    }

    private boolean passwordRegExMatcher(String password, String regex) {
        if(regex == null) return true;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private boolean isFirstLogin(User user) {
        Validate.isEntityOrIdNotNull(user, "USER_OR_USER_ID_IS_NULL");
        return user.getLastLoginDate() == null;
    }


    public void setLastLoginDate(User user) {
        Validate.isEntityOrIdNotNull(user, "USER_OR_USER_ID_IS_NULL");
        user.setLastLoginDate(new Date());
        super.update(user, user.getUsername());
    }

    @Override
    public Role findTopPriorityRoleByUser(User user) {
        return user.getRoles().stream().min(Comparator.comparingInt(Role::getPriority)).orElse( null );
    }

    @Transactional
    @Override
    public ResponseEntity<?> changePasswordFromLogin(UserPasswordChangingDTO user, String username) {
        User userFromDB = readUser(username);
        if (!bCryptPasswordEncoder.matches(user.getOldPassword(), userFromDB.getPassword())) {
            throw new AppException("OLD_PASSWORD_ERROR");
        }
        Validate.isEquals(user.getNewPassword(), user.getConfirmNewPassword(), "PASSWORD_AND_CONFIRM_PASSWORD_INEQUALITY");
        checkIfNewPasswordEqualCurrentPassword(user.getNewPassword(), userFromDB.getPassword());
        if (!passwordRegExMatcher(user.getNewPassword(), findTopPriorityRoleByUser(userFromDB).getRegex())) {
            throw new AppException("PASSWORD_DOES_NOT_MATCH_APPROPRIATE_REGEX");
        }
        userFromDB.setPassword(bCryptPasswordEncoder.encode(user.getNewPassword()));
        userFromDB.setLastLoginDate(new Date());
        super.update(userFromDB, username);
        return new ResponseEntity<>(DescriptionUtils.getResponseDescription("PASSWORD_CHANGED_SUCCESSFULLY"), HttpStatus.OK);

    }

}
