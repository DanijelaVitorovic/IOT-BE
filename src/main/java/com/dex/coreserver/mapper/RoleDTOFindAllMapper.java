package com.dex.coreserver.mapper;

import com.dex.coreserver.dto.ActionDTO;
import com.dex.coreserver.dto.RoleDTO;
import com.dex.coreserver.model.Role;
import com.dex.coreserver.model.enums.Actions;
import com.dex.coreserver.util.DescriptionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RoleDTOFindAllMapper {

    public RoleDTO map(Role from) {
        if (from == null) {
            return null;
        }
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.setId(from.getId());
        roleDTO.setRoleName(from.getRoleName());
        List<Actions> actions = from.getActions();
        List<ActionDTO> actionsDTOList = new ArrayList<>();
        for (Actions a : actions){
            ActionDTO actionDTO = new ActionDTO(a.toString(), DescriptionUtils.getActionDescription(a.toString()));
            actionsDTOList.add(actionDTO);
        }
        roleDTO.setActions(actionsDTOList);
        roleDTO.setRegex(from.getRegex());
        roleDTO.setRegexDescription(from.getRegexDescription());
        return roleDTO;
    }

    public List<RoleDTO> map(Collection<Role> from) {
        return from.stream().map(this::map).collect(Collectors.toList());
    }

}
