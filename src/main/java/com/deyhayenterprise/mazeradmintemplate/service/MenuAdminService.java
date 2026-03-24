package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;

import com.deyhayenterprise.mazeradmintemplate.entity.AppMenu;
import com.deyhayenterprise.mazeradmintemplate.entity.MenuOption;
import com.deyhayenterprise.mazeradmintemplate.web.form.AppMenuForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.MenuOptionForm;

public interface MenuAdminService {

    List<AppMenu> findAllMenus();

    AppMenu createMenu(AppMenuForm form);

    MenuOption createOption(MenuOptionForm form);
}

