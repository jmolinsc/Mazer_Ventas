package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.deyhayenterprise.mazeradmintemplate.entity.Role;
import com.deyhayenterprise.mazeradmintemplate.service.dto.ModuleActionPermissionView;
import com.deyhayenterprise.mazeradmintemplate.service.dto.MenuOptionsGroupView;

public interface RoleService {

    List<Role> findAllRoles();

    Optional<Role> findRole(Long roleId);

    Set<Long> findAssignedOptionIds(Long roleId);

    Set<Long> findAssignedMovtipoIds(Long roleId);

    Set<Long> findAssignedAfectarOptionIds(Long roleId);

    Set<Long> findAssignedCancelarOptionIds(Long roleId);

    List<MenuOptionsGroupView> buildPermissionCatalog();

    List<ModuleActionPermissionView> buildActionPermissionCatalog();

    void updateRolePermissions(Long roleId, Set<Long> optionIds, Set<Long> movtipoIds);

    Role createRole(String codigoInput, String nombreInput, String descripcionInput);
}
