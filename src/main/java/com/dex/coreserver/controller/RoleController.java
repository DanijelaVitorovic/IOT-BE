package com.dex.coreserver.controller;

import com.dex.coreserver.dto.ActionDTO;
import com.dex.coreserver.dto.RoleDTO;
import com.dex.coreserver.model.Role;
import com.dex.coreserver.service.MapValidationErrorService;
import com.dex.coreserver.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/role")
public class RoleController extends BasicController{

    @Autowired
    private RoleService roleService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @Override
    protected String getClassName() {
        return Role.class.getName();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Role role, BindingResult result, Principal principal,
                                    @RequestHeader(value = "locale", required = false) String locale) {
        mapValidationErrorService.MapValidationService(result);
        Role createdRole = roleService.create(role, principal.getName());
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<?> update(@Valid @RequestBody Role role, BindingResult result, Principal principal,
                                    @RequestHeader(value = "locale", required = false) String locale) {
        mapValidationErrorService.MapValidationService(result);
        RoleDTO updatedRole = roleService.updateDTO(role, principal.getName());
        return new ResponseEntity<>(updatedRole, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> findAll(Principal principal, @RequestHeader(value = "locale", required = false) String locale) {
        List<Role> roleList = roleService.findAll(principal.getName());
        return new ResponseEntity<>(roleList, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id, Principal principal,
                                      @RequestHeader(value = "locale", required = false) String locale) {
        Role role = roleService.findById(id);
        return new ResponseEntity<>(role, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Long id, Principal principal,
                       @RequestHeader(value = "locale", required = false) String locale) {
        roleService.delete(id, principal.getName());
    }

    @GetMapping("/find-all-roles-dto")
    public ResponseEntity<?> findAllRolesDTO(Principal principal, @RequestHeader(value = "locale", required = false) String locale) {
        List<RoleDTO> roleDTOList = roleService.findAllRolesDTO(principal.getName());
        return new ResponseEntity<>(roleDTOList, HttpStatus.OK);
    }

    @GetMapping("/{pageNumber}/{pageSize}")
    public ResponseEntity<?> findAllPageable(@PathVariable  int pageNumber, @PathVariable  int pageSize) {
        Page page = roleService.findAllByPageAndSize(pageNumber,pageSize);
        return new ResponseEntity<>( page, HttpStatus.OK );
    }

    @GetMapping("/find-all-roles-hash-map-list")
    public ResponseEntity<?> findAllRolesHash(Principal principal, @RequestHeader(value = "locale", required = false) String locale) {
        List<Map> rolesMap = roleService.findAllRolesHashMapList(principal.getName());
        return new ResponseEntity<>( rolesMap, HttpStatus.OK );
    }

    @GetMapping("/find-all-actions")
    public ResponseEntity<?> findAllActions(Principal principal, @RequestHeader(value = "locale", required = false) String locale) {
        List<ActionDTO> actions = roleService.findAllActions(principal.getName());
        return new ResponseEntity<>( actions, HttpStatus.OK );
    }

    @PostMapping("/create-and-return-role-dto")
    public ResponseEntity<?> createAndReturnRoleDTO(@Valid @RequestBody Role role, BindingResult result, Principal principal,
                                                    @RequestHeader(value = "locale", required = false) String locale) {
        mapValidationErrorService.MapValidationService(result);
        RoleDTO createdRole = roleService.createAndReturnRoleDTO(role, principal.getName());
        return new ResponseEntity<>(createdRole, HttpStatus.CREATED);
    }


}
