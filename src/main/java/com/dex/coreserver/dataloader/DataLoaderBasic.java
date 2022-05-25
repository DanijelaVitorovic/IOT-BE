package com.dex.coreserver.dataloader;

import com.dex.coreserver.model.Role;
import com.dex.coreserver.model.User;
import com.dex.coreserver.model.enums.Actions;
import com.dex.coreserver.repository.RoleRepository;
import com.dex.coreserver.repository.UserRepository;
import com.dex.coreserver.util.ApplicationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

public class DataLoaderBasic {

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    public void loadBasicData() {
        List<User> userList = userRepository.findAll();

        if (userList.size() == 0) {

            Role adminRole = new Role();
            adminRole.setRoleName("admin");
            adminRole.setPriority(1);
            Role regularUser = new Role();
            regularUser.setRoleName("regular-user");
            regularUser.setPriority(2);

            List<Actions> actions = Arrays.asList(Actions.values());
            List<Actions> regularUserActions = Arrays.asList(Actions.MENU_USER,Actions.MENU_ROLE,Actions.USER_UPDATE);
            adminRole.setActions(actions);
            regularUser.setActions(regularUserActions);
            adminRole.setRegex( ApplicationUtils.DEFAULT_REGEX );
            adminRole.setRegexDescription( ApplicationUtils.DEFAULT_REGEX_DESCRIPTION );
            roleRepository.save(adminRole);
            roleRepository.save(regularUser);

            Set<Role> adminRoleList = new HashSet<>();
            adminRoleList.add(adminRole);
            adminRoleList.add(regularUser);

            Set<Role> regularRoleList = new HashSet<>();
            regularRoleList.add(regularUser);

            User adminUser = new User();
            adminUser.setActive(true);
            adminUser.setFirstName("Admin");
            adminUser.setLastName("Admin");
            adminUser.setUsername("admin");
            adminUser.setPassword(bCryptPasswordEncoder.encode("admin"));
            adminUser.setEmail("admin@mail.com");
            adminUser.setCreatedAt(new Date());
            adminUser.setUseGoogle2f(false);
            adminUser.setRoles(adminRoleList);
            Calendar passwordExpirationDate = Calendar.getInstance();
            passwordExpirationDate.setTime(new Date());
            passwordExpirationDate.add(Calendar.YEAR,1);
            adminUser.setPasswordExpirationDate(passwordExpirationDate.getTime());
            adminUser.setLastLoginDate( new Date() );
            userRepository.save(adminUser);

            User regular = new User();
            regular.setActive(true);
            regular.setFirstName("Regular");
            regular.setLastName("Regular");
            regular.setUsername("regular");
            regular.setPassword(bCryptPasswordEncoder.encode("regular"));
            regular.setEmail("regular@mail.com");
            regular.setCreatedAt(new Date());
            regular.setUseGoogle2f(false);
            regular.setRoles(regularRoleList);
            Calendar passwordExpirationDateRegular = Calendar.getInstance();
            passwordExpirationDateRegular.setTime(new Date());
            passwordExpirationDateRegular.add(Calendar.YEAR,1);
            regular.setPasswordExpirationDate(passwordExpirationDateRegular.getTime());
            userRepository.save(regular);

        }
    }
}
