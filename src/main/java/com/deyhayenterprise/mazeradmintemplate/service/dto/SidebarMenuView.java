package com.deyhayenterprise.mazeradmintemplate.service.dto;

import java.util.List;

public record SidebarMenuView(String title, String icon, String url, List<SidebarOptionView> options) {

    public boolean directLink() {
        return url != null && !url.isBlank();
    }

    public boolean activeForPath(String currentPath) {
        if (directLink()) {
            return new SidebarOptionView(title, url).activeForPath(currentPath);
        }
        return options.stream().anyMatch(option -> option.activeForPath(currentPath));
    }

    public boolean openForPath(String currentPath) {
        return !directLink() && activeForPath(currentPath);
    }
}

