package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.deyhayenterprise.mazeradmintemplate.entity.Role;
import com.deyhayenterprise.mazeradmintemplate.service.dto.MenuOptionsGroupView;

public interface RoleService {

    List<Role> findAllRoles();

    Optional<Role> findRole(Long roleId);

    Set<Long> findAssignedOptionIds(Long roleId);

    List<MenuOptionsGroupView> buildPermissionCatalog();

    void updateRolePermissions(Long roleId, Set<Long> optionIds);

    Role createRole(String codigoInput, String nombreInput, String descripcionInput);
}
