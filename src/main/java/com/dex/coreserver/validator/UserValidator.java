package com.dex.coreserver.validator;

import com.dex.coreserver.model.User;
import com.dex.coreserver.util.Validate;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return User.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Validate.isNotNull(o, "USER_IS_NULL");
        User user = (User) o;
        Validate.isNotNull(user.getUsername(), "USERNAME_IS_NULL");
        Validate.isNotNull(user.getPassword(), "PASSWORD_IS_NULL");
        if(user.getPassword().length()<6){
            errors.rejectValue("password","Length","Lozinka mora sadržati najmanje 6 karaktera");
        }

        if(!user.getPassword().equals(user.getConfirmPassword())){
            errors.rejectValue("confirmPassword","Match","Vrednosti za lozinku i ponovljenu lozinku nisu iste");
        }
    }
}
