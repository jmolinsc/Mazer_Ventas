package com.deyhayenterprise.mazeradmintemplate.service.dto;

public record ModuleActionPermissionView(
        Long menuId,
        String menuCode,
        String menuName,
        Long afectarOptionId,
        Long cancelarOptionId,
        boolean hasAfectarOption,
        boolean hasCancelarOption
) {
}

