package com.deyhayenterprise.mazeradmintemplate.web.form;

import java.util.LinkedHashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolePermissionUpdateForm {

    private Set<Long> optionIds = new LinkedHashSet<>();
    private Set<Long> afectarOptionIds = new LinkedHashSet<>();
    private Set<Long> cancelarOptionIds = new LinkedHashSet<>();
    private Set<Long> movtipoIds = new LinkedHashSet<>();
}

