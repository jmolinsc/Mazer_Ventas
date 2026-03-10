package com.deyhayenterprise.mazeradmintemplate.security;

import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.AppUser;
import com.deyhayenterprise.mazeradmintemplate.repository.AppUserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .disabled(!user.isActivo())
                .accountLocked(user.isBloqueado())
                .authorities(user.getRoles().stream()
                        .filter(role -> role.isActivo())
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getCodigo().toUpperCase()))
                        .collect(Collectors.toSet()))
                .build();
    }
}

