package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;
import java.util.Optional;

import com.deyhayenterprise.mazeradmintemplate.entity.AppUser;
import com.deyhayenterprise.mazeradmintemplate.web.form.UserCreateForm;

public interface AppUserService {

    List<AppUser> findAllUsers();

    Optional<AppUser> findByUsername(String username);

    AppUser createUser(UserCreateForm form);
}
