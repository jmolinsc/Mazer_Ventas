package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.List;

import com.deyhayenterprise.mazeradmintemplate.service.dto.SidebarSectionView;

public interface MenuService {

    List<SidebarSectionView> buildSidebarForUser(String username);
}
