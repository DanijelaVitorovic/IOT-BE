package com.dex.coreserver.service;

import com.dex.coreserver.dto.UserPasswordChangingDTO;
import com.dex.coreserver.model.Role;
import com.dex.coreserver.model.User;
import com.dex.coreserver.payload.LoginRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Map;

public interface UserService extends BasicService<User>{

    User updatePassword(User user, String username);

    User deactivateUser(Long id,String username);
    User activateUser(Long id,String username);


    Page findAllByPageAndSize(int pageNumber, int pageSize);
    User deactivate2fa(Long id,String username);

    String register2fa(User loggedUser, Long id);

    User confirm2fa(String verifyCode, User loggeduser, Long id) throws IOException;

    User findByUsername(String username);

    Map<String,Object> authenticateUser(LoginRequest loginRequest);

    Map<String,String> getAppInfo();


    String generateUsername(String firstName, String lastName, String principalName);

    boolean isUsernameGeneratorActive();

    String generateRandomPassword(String username, int... passwordLength);

    User changePassword(User userForPassChange, String username);

    boolean shouldChangePassword(User user, String password);

    void setLastLoginDate(User user);

    Role findTopPriorityRoleByUser(User user);

    ResponseEntity<?> changePasswordFromLogin(UserPasswordChangingDTO user,String username);
}
