package com.deyhayenterprise.mazeradmintemplate.web;

import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.deyhayenterprise.mazeradmintemplate.service.AppUserService;
import com.deyhayenterprise.mazeradmintemplate.service.MenuService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@ControllerAdvice(annotations = Controller.class)
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final MenuService menuService;
    private final AppUserService appUserService;

    @ModelAttribute("sidebarSections")
    public List<?> sidebarSections(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return List.of();
        }
        return menuService.buildSidebarForUser(authentication.getName());
    }

    @ModelAttribute("currentUser")
    public Object currentUser(Authentication authentication) {
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return appUserService.findByUsername(authentication.getName()).orElse(null);
    }

    @ModelAttribute("currentPath")
    public String currentPath(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isBlank() && uri.startsWith(contextPath)) {
            uri = uri.substring(contextPath.length());
        }
        if (uri == null || uri.isBlank()) {
            return "/";
        }
        return uri.endsWith("/") && uri.length() > 1 ? uri.substring(0, uri.length() - 1) : uri;
    }
}

