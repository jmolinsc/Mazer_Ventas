package com.deyhayenterprise.mazeradmintemplate.service.impl;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.AppUser;
import com.deyhayenterprise.mazeradmintemplate.entity.Role;
import com.deyhayenterprise.mazeradmintemplate.repository.AppUserRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.RoleRepository;
import com.deyhayenterprise.mazeradmintemplate.service.AppUserService;
import com.deyhayenterprise.mazeradmintemplate.web.form.UserCreateForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppUserServiceImpl implements AppUserService {

    private final AppUserRepository appUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<AppUser> findAllUsers() {
        return appUserRepository.findAllWithRoles();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppUser> findByUsername(String username) {
        return appUserRepository.findByUsernameIgnoreCase(username);
    }

    @Override
    @Transactional
    public AppUser createUser(UserCreateForm form) {
        if (appUserRepository.existsByUsernameIgnoreCase(form.getUsername())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese nombre.");
        }
        if (appUserRepository.existsByEmailIgnoreCase(form.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con ese correo.");
        }

        Set<Role> roles = new LinkedHashSet<>(roleRepository.findAllById(form.getRoleIds()));
        if (roles.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un rol valido.");
        }

        AppUser user = new AppUser();
        user.setUsername(form.getUsername().trim());
        user.setNombreCompleto(form.getNombreCompleto().trim());
        user.setEmail(form.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setActivo(true);
        user.setBloqueado(false);
        user.setRoles(roles);

        return appUserRepository.save(user);
    }
}

