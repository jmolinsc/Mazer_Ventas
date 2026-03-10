package com.deyhayenterprise.mazeradmintemplate.service.dto;

public record SidebarOptionView(String title, String url) {

    public boolean activeForPath(String currentPath) {
        String normalizedOption = normalize(url);
        String normalizedCurrent = normalize(currentPath);
        return normalizedCurrent.equals(normalizedOption) || normalizedCurrent.startsWith(normalizedOption + "/");
    }

    private static String normalize(String rawPath) {
        if (rawPath == null || rawPath.isBlank()) {
            return "/";
        }
        String value = rawPath.strip();
        if ("/".equals(value) || "/index.html".equalsIgnoreCase(value)) {
            return "/index";
        }
        return value.endsWith("/") && value.length() > 1 ? value.substring(0, value.length() - 1) : value;
    }
}

