package com.dex.coreserver.service;

import com.dex.coreserver.model.User;
import com.dex.coreserver.repository.UserRepository;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    public void setUp(){
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void userService_findById(){
        User user = new User();
        user.setId(1L);
        BDDMockito.given(userRepository.getOne(1L)).willReturn(user);
        User testObject = userService.findById(1L);
        Assert.assertNotNull(testObject);
        Assert.assertEquals(user.getId(),testObject.getId());
    }
}
