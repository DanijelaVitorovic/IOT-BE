package com.dex.coreserver.service;

import com.dex.coreserver.dto.ActionDTO;
import com.dex.coreserver.dto.RoleDTO;
import com.dex.coreserver.model.Role;
import org.springframework.data.domain.Page;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RoleService extends BasicService<Role> {


    List<ActionDTO> findAllActions(String username);

    List<RoleDTO> findAllRolesDTO(String username);

    @Transactional
    RoleDTO updateDTO(Role role, String username);

    RoleDTO createAndReturnRoleDTO(Role role, String name);

    Page findAllByPageAndSize(int pageNumber, int pageSize);

    List<Map> findAllRolesHashMapList(String name);

    Role findFirstByUserId(Long userId);
}
