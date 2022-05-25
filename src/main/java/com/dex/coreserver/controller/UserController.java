package com.dex.coreserver.controller;

import com.dex.coreserver.dto.UserPasswordChangingDTO;
import com.dex.coreserver.model.User;
import com.dex.coreserver.model.enums.Actions;
import com.dex.coreserver.payload.JWTLoginSuccessResponse;
import com.dex.coreserver.payload.LoginRequest;
import com.dex.coreserver.payload.RefreshTokenRequest;
import com.dex.coreserver.security.JwtTokenProvider;
import com.dex.coreserver.security.JwtUtils;
import com.dex.coreserver.service.MapValidationErrorService;
import com.dex.coreserver.service.UserService;
import com.dex.coreserver.util.DescriptionUtils;
import dev.samstevens.totp.code.CodeVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CodeVerifier verifier;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @PostMapping("/sign-up/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult result,
                                   @RequestHeader(value = "locale", required = false) String locale){
        mapValidationErrorService.MapValidationService(result);
        Map<String, Object> response = userService.authenticateUser(loginRequest);
        return ResponseEntity.ok(new JWTLoginSuccessResponse(true, (String) response.get("accessToken"),
               (String) response.get( "refreshToken" ), (Boolean) response.get("shouldChangePassword")));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest){
        JWTLoginSuccessResponse response = tokenProvider.refreshToken(refreshTokenRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<?> createNewUser(@Valid @RequestBody User user, BindingResult result, Principal principal,
                                           @RequestHeader(value = "locale", required = false) String locale) {
        mapValidationErrorService.MapValidationService(result);
        User createdUser = userService.create(user, principal.getName());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody User user, BindingResult result, Principal principal,
                                    @RequestHeader(value = "locale", required = false) String locale) {
        mapValidationErrorService.MapValidationService(result);
        User updatedUser = userService.update(user, principal.getName());
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody User user, BindingResult result, Principal principal,
                                            @RequestHeader(value = "locale", required = false) String locale) {
        mapValidationErrorService.MapValidationService(result);
        User updatedUser = userService.updatePassword(user, principal.getName());
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @GetMapping(value = {"/generate-random-password", "/generate-random-password/{passwordLength}"})
    public ResponseEntity<?> generateRandomPassword(@PathVariable(required = false) Integer passwordLength, Principal principal,
                                                    @RequestHeader(value = "locale", required = false) String locale) {
        String username = principal.getName();
        String generatedPassword = passwordLength == null ?
                userService.generateRandomPassword(username) : userService.generateRandomPassword( username, passwordLength);
        return new ResponseEntity<>(generatedPassword, HttpStatus.OK);
    }

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody User user, BindingResult result, Principal principal,
                                            @RequestHeader(value = "locale", required = false) String locale) {
        mapValidationErrorService.MapValidationService(result);
        User updatedUser = userService.changePassword(user, principal.getName());
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    @PutMapping("/password-change-from-login")
    public ResponseEntity<?> changePasswordFromLogin(@Valid @RequestBody UserPasswordChangingDTO user,
                                                     BindingResult result, Principal principal,
                                                     @RequestHeader(value = "locale", required = false) String locale) {
        mapValidationErrorService.MapValidationService(result);
        return userService.changePasswordFromLogin(user, principal.getName());
    }

    @GetMapping
    public ResponseEntity<?> findAll(Principal principal, @RequestHeader(value = "locale", required = false) String locale) {
         List<User> userList = userService.findAll(principal.getName());
         return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    private ResponseEntity<?> findUserById(@PathVariable Long id, Principal principal,
                                           @RequestHeader(value = "locale", required = false) String locale) {
        User user = userService.findById(id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PatchMapping("/deactivate/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deactivateUser(@PathVariable Long id, Principal principal,
                                            @RequestHeader(value = "locale", required = false) String locale){
        User deactivatedUser = userService.deactivateUser(id,principal.getName());
        return new ResponseEntity<>(deactivatedUser, HttpStatus.OK);
    }

    @PatchMapping("/activate/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> activateUser(@PathVariable Long id, Principal principal,
                                          @RequestHeader(value = "locale", required = false) String locale){
        User activatedUser = userService.activateUser(id,principal.getName());
        return new ResponseEntity<>(activatedUser, HttpStatus.OK);
    }

    @GetMapping("/{pageNumber}/{pageSize}")
    public ResponseEntity<?> findAllPageable(@PathVariable  int pageNumber, @PathVariable  int pageSize) {
        Page page = userService.findAllByPageAndSize(pageNumber,pageSize);
        return new ResponseEntity<Page>(page, HttpStatus.OK);
    }

    @GetMapping("/get-allowed-actions")
    public ResponseEntity<?> getAllowedActions(Principal principal, Long id,
                                               @RequestHeader(value = "locale", required = false) String locale) {
        List<Actions> allowedActionsList = userService.getAllowedActions(principal.getName(), id);
        return new ResponseEntity<>(allowedActionsList, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable Long id, Principal principal,
                           @RequestHeader(value = "locale", required = false) String locale){
        userService.delete(id, principal.getName());
    }

    @GetMapping("/register-2fa/{id}")
    public ResponseEntity<?> register2fa(@PathVariable Long id, @RequestHeader(value = "locale", required = false) String locale){
        User loggedUser = getUserFromContext();
        String qrCodeImage = userService.register2fa(loggedUser, id);
        return new ResponseEntity<>( qrCodeImage, HttpStatus.CREATED );
    }

    @PostMapping("/confirm-2fa/{id}")
    public ResponseEntity<?> confirm2Fa(HttpServletResponse response, @RequestBody String verifyCode, @PathVariable Long id,
                                        @RequestHeader(value = "locale", required = false) String locale) throws IOException{
        User loggedUser = getUserFromContext();
        User userForConfirmation = userService.confirm2fa(verifyCode, loggedUser, id);

        if (userForConfirmation == null){
            response.sendError(HttpStatus.BAD_REQUEST.value(), DescriptionUtils.getErrorDescription("INVALID_2FA_CODE"));
            return ResponseEntity.badRequest().body(DescriptionUtils.getErrorDescription("INVALID_2FA_CODE"));
        }

        return ResponseEntity.ok(userForConfirmation);
    }

    @PostMapping("/sign-up/verify-2fa/{id}")
    public void verifyPostOf2Fa(HttpServletResponse response, @RequestBody String verifyCode, @PathVariable Long id,
                                @RequestHeader(value = "locale", required = false) String locale) throws IOException {
        User userForVerification = userService.findById(id);

        if (verifier.isValidCode(userForVerification.getGoogle2FaSecret(), verifyCode)) {
            userForVerification.setGoogle2FaRequired(false);
            setJwt2faToken(response, userForVerification.getId());
        } else {
            response.sendError(HttpStatus.BAD_REQUEST.value(), DescriptionUtils.getErrorDescription("INVALID_2FA_CODE"));
        }
    }

    @PatchMapping("/deactivate-2fa/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> deactivate2fa(@PathVariable Long id, Principal principal,
                                           @RequestHeader(value = "locale", required = false) String locale){
        User deactivatedUser2fa = userService.deactivate2fa(id, principal.getName());
        return new ResponseEntity<>(deactivatedUser2fa, HttpStatus.OK);
    }

    @GetMapping("/generate-username/{firstName}/{lastName}")
    public ResponseEntity<?> getUsername(@PathVariable String firstName, @PathVariable String lastName, Principal principal,
                                         @RequestHeader(value = "locale", required = false) String locale) {
        String username = userService.generateUsername(firstName, lastName, principal.getName());
        return new ResponseEntity<>(username, HttpStatus.OK);
    }

    @GetMapping("/find-app-version-and-locale")
    public ResponseEntity<?> findAppVersionAndLocale() {
        Map<String, String> response = userService.getAppInfo();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/find-token-intervals")
    public ResponseEntity<?> findTokenIntervals() {
        Map<String, Long> response = tokenProvider.getTokenIntervals();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/find-username-generator-signal")
    public ResponseEntity<?> findUsernameGeneratorSignal() {
        boolean response = userService.isUsernameGeneratorActive();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private User getUserFromContext() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private void setJwt2faToken(HttpServletResponse response, Long userId){
        JwtUtils jwtUtils = new JwtUtils();
        response.setHeader("X-2FA-TOKEN-UPDATE", jwtUtils.generate2faToken(userId));
    }

}
