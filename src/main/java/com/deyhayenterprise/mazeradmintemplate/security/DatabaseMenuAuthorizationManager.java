package com.deyhayenterprise.mazeradmintemplate.security;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import com.deyhayenterprise.mazeradmintemplate.repository.MenuOptionRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DatabaseMenuAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final Set<String> ALWAYS_ALLOWED = Set.of("/", "/index", "/index.html");

    private final MenuOptionRepository menuOptionRepository;

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        Authentication authentication = authenticationSupplier.get();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken || !authentication.isAuthenticated()) {
            return new AuthorizationDecision(false);
        }

        String username = authentication.getName();
        String requestPath = normalize(context.getRequest().getRequestURI(), context.getRequest().getContextPath());
        if (ALWAYS_ALLOWED.contains(requestPath)) {
            return new AuthorizationDecision(true);
        }

        List<String> allowedUrls = menuOptionRepository.findAllowedUrlsByUsername(username);
        boolean granted = allowedUrls.stream()
                .map(DatabaseMenuAuthorizationManager::normalize)
                .anyMatch(allowedUrl -> matches(requestPath, allowedUrl));

        return new AuthorizationDecision(granted);
    }

    private static boolean matches(String requestPath, String allowedUrl) {
        if (requestPath.equals(allowedUrl)) {
            return true;
        }
        return !"/".equals(allowedUrl) && requestPath.startsWith(allowedUrl + "/");
    }

    private static String normalize(String rawPath, String contextPath) {
        String normalized = normalize(rawPath);
        if (contextPath != null && !contextPath.isBlank() && normalized.startsWith(contextPath)) {
            normalized = normalized.substring(contextPath.length());
        }
        return normalize(normalized);
    }

    private static String normalize(String rawPath) {
        if (rawPath == null || rawPath.isBlank()) {
            return "/";
        }
        String value = rawPath.strip();
        value = value.replaceAll("//+", "/");
        if (value.endsWith("/") && value.length() > 1) {
            value = value.substring(0, value.length() - 1);
        }
        return value.isBlank() ? "/" : value;
    }

    public Set<String> findAllowedUrls(String username) {
        return menuOptionRepository.findAllowedUrlsByUsername(username).stream()
                .map(DatabaseMenuAuthorizationManager::normalize)
                .collect(Collectors.toSet());
    }
}
