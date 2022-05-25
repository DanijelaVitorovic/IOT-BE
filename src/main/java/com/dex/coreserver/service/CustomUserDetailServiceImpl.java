package com.dex.coreserver.service;

import com.dex.coreserver.exceptions.AppException;
import com.dex.coreserver.exceptions.AppResourceNotFoundException;
import com.dex.coreserver.model.User;
import com.dex.coreserver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Value("${username.generator}")
    private boolean usernameGeneratorActive;

    @Override
    public UserDetails loadUserByUsername(String username){
        User user = userRepository.findByUsername(username);
        if(user == null) throw new AppResourceNotFoundException("USER_DOES_NOT_EXIST_FOR_USERNAME");
        if(!user.isActive()) throw new AppException( "USER_IS_DEACTIVATED" );
        if(usernameGeneratorActive && user.isPasswordExpired()) throw new AppException( "PASSWORD_EXPIRED" );
        return user;
    }

    @Transactional
    public User loadUserById(Long id){
        User user = userRepository.getById(id);
        if(user == null) throw new AppResourceNotFoundException("USER_DOES_NOT_EXIST_FOR_USERNAME");
        if(!user.isActive()) throw new AppException( "USER_IS_DEACTIVATED" );
        if(usernameGeneratorActive && user.isPasswordExpired()) throw new AppException( "PASSWORD_EXPIRED" );
        return user;
    }


}
