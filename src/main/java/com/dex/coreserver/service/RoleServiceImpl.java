package com.dex.coreserver.service;

import com.dex.coreserver.dto.ActionDTO;
import com.dex.coreserver.dto.RoleDTO;
import com.dex.coreserver.exceptions.AppException;
import com.dex.coreserver.mapper.RoleDTOFindAllMapper;
import com.dex.coreserver.model.Role;
import com.dex.coreserver.model.User;
import com.dex.coreserver.model.enums.Actions;
import com.dex.coreserver.repository.RoleRepository;
import com.dex.coreserver.util.ApplicationUtils;
import com.dex.coreserver.util.DescriptionUtils;
import com.dex.coreserver.util.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import javax.persistence.PersistenceException;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
public class RoleServiceImpl extends BasicServiceImpl<Role> implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    protected JpaRepository<Role, Long> getRepository() {
        return roleRepository;
    }

    @Override
    protected Actions getCreateAction() {
        return Actions.ROLE_CREATE;
    }

    @Override
    protected Actions getDeleteAction() {
        return Actions.ROLE_DELETE;
    }

    @Override
    protected Actions getUpdateAction() {
        return Actions.ROLE_UPDATE;
    }

    @Override
    protected Actions getFindAllAction() {
        return Actions.ROLE_FIND_ALL;
    }

    @Override
    protected void validate(Role entity, User user) throws AppException {
        Validate.isNotNull(entity, "ROLE_IS_NULL");
    }

    @Override
    protected void validatePersistenceException(DataAccessException e) throws AppException, PersistenceException {
        if (e.getMostSpecificCause().getMessage().indexOf("unq_role_name") > 0) {
            throw new AppException("ROLE_NAME_MUST_BE_UNIQUE");
        }
        throw new AppException("UNKNOWN_EXCEPTION", e);
    }

    @Override
    public List<ActionDTO> findAllActions(String username) {
        List<Actions> actions = Arrays.asList(Actions.values());
        actions = ApplicationUtils.filterActionsList(actions);
        return getActionDTOList(actions);
    }

    @Override
    public List<RoleDTO> findAllRolesDTO(String username) {
        List<Role> roleList = super.findAll(username);
        RoleDTOFindAllMapper roleDTOFindAllMapper = new RoleDTOFindAllMapper();
        return roleDTOFindAllMapper.map(roleList);
    }

    @Override
    @Transactional
    public RoleDTO createAndReturnRoleDTO(Role role, String username) {
        if(role.getRegex() == null) role.setRegex( ApplicationUtils.DEFAULT_REGEX );
        if(role.getRegexDescription() == null) role.setRegexDescription( ApplicationUtils.DEFAULT_REGEX_DESCRIPTION );
        Role createdRole = super.create(role, username);
        RoleDTOFindAllMapper roleDTOFindAllMapper = new RoleDTOFindAllMapper();
        return roleDTOFindAllMapper.map(createdRole);
    }

    @Override
    public List<Map> findAllRolesHashMapList(String username) {
        List<Role> roleList = super.findAll(username);
        List<Map> listOfRoleHashMaps = new LinkedList<>();
        for (Role role : roleList){
            Map<String, String> map = new HashMap<>();
            map.put("id", role.getId().toString());
            map.put("roleName", role.getRoleName());
            listOfRoleHashMaps.add(map);
        }
        return listOfRoleHashMaps;
    }

    private List<ActionDTO> getActionDTOList(List<Actions> actions) {
        List<ActionDTO> actionDTOList = new LinkedList<>();
        for (Actions a : actions) {
            ActionDTO actionDTO = new ActionDTO(a.toString(), DescriptionUtils.getActionDescription(a.toString()));
            actionDTOList.add(actionDTO);
        }
        return actionDTOList;
    }

    @Override
    public Page findAllByPageAndSize(int pageNumber, int pageSize) {
        Page page = super.findAllByPageAndSize(pageNumber, pageSize);
        Page<Role> pageRoles = page != null ? page : new PageImpl<>( new ArrayList<>() );
        RoleDTOFindAllMapper mapper = new RoleDTOFindAllMapper();
        return pageRoles.map(mapper::map);
    }

    @Transactional
    @Override
    public RoleDTO updateDTO(Role role, String username) {
        Validate.isEntityOrIdNotNull( role, "ROLE_OR_ROLE_ID_IS_NULL" );
        Role roleUpdated = update(role, username);
        RoleDTOFindAllMapper mapper = new RoleDTOFindAllMapper();
        return mapper.map(roleUpdated);
    }

    @Override
    @Transactional
    public void delete(Long id, String username) {
        Role roleForDelete = findById(id);
        for(User user : new LinkedList<>(roleForDelete.getUsers())){
            roleForDelete.removeUser(user);
        }
        super.delete(id, username);
    }

    @Override
    public Role findFirstByUserId(Long userId) {
        return roleRepository.findFirstByUsersId( userId );
    }
}
