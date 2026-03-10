package com.deyhayenterprise.mazeradmintemplate.service.dto;

import java.util.List;

public record MenuOptionsGroupView(Long menuId, String menuName, String section, String icon, List<OptionPermissionView> options) {
}

