package com.dex.coreserver.controller;

import com.dex.coreserver.model.User;
import com.dex.coreserver.security.JwtTokenProvider;
import com.dex.coreserver.service.MapValidationErrorService;
import com.dex.coreserver.service.UserServiceImpl;
import com.dex.coreserver.validator.UserValidator;
import dev.samstevens.totp.code.CodeVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.security.Principal;
import java.util.Arrays;


@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserServiceImpl userService;
    @MockBean
    private MapValidationErrorService mapValidationErrorService;
    @MockBean
    UserValidator userValidator;
    @MockBean
    private JwtTokenProvider tokenProvider;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private CodeVerifier verifier;
    @MockBean
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Test
    public void findAllUsers_test() throws Exception{
        BDDMockito.given(userService.findAll(Mockito.anyString())).willReturn(Arrays.asList(new User()));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user").principal(new Principal() {
            @Override
            public String getName() {
                return "admin";
            }
        }))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());
    }

}