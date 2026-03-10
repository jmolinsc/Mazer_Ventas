package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.AppMenu;
import com.deyhayenterprise.mazeradmintemplate.entity.MenuOption;
import com.deyhayenterprise.mazeradmintemplate.repository.MenuOptionRepository;
import com.deyhayenterprise.mazeradmintemplate.service.dto.SidebarMenuView;
import com.deyhayenterprise.mazeradmintemplate.service.dto.SidebarOptionView;
import com.deyhayenterprise.mazeradmintemplate.service.dto.SidebarSectionView;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuOptionRepository menuOptionRepository;

    @Transactional(readOnly = true)
    public List<SidebarSectionView> buildSidebarForUser(String username) {
        Map<String, List<SidebarMenuView>> sections = new LinkedHashMap<>();
        Map<Long, List<MenuOption>> optionsByMenu = new LinkedHashMap<>();
        Map<Long, AppMenu> menus = new LinkedHashMap<>();

        for (MenuOption option : menuOptionRepository.findVisibleOptionsByUsername(username)) {
            AppMenu menu = option.getMenu();
            menus.putIfAbsent(menu.getId(), menu);
            optionsByMenu.computeIfAbsent(menu.getId(), key -> new ArrayList<>()).add(option);
        }

        for (Map.Entry<Long, List<MenuOption>> entry : optionsByMenu.entrySet()) {
            AppMenu menu = menus.get(entry.getKey());
            List<MenuOption> options = entry.getValue();

            SidebarMenuView menuView;
            if (options.size() == 1) {
                MenuOption singleOption = options.get(0);
                menuView = new SidebarMenuView(menu.getNombre(), defaultIcon(menu.getIcono()), singleOption.getUrl(), List.of());
            } else {
                menuView = new SidebarMenuView(
                        menu.getNombre(),
                        defaultIcon(menu.getIcono()),
                        null,
                        options.stream()
                                .map(option -> new SidebarOptionView(option.getNombre(), option.getUrl()))
                                .toList());
            }

            sections.computeIfAbsent(menu.getSeccion(), key -> new ArrayList<>()).add(menuView);
        }

        return sections.entrySet().stream()
                .map(entry -> new SidebarSectionView(entry.getKey(), entry.getValue()))
                .toList();
    }

    private String defaultIcon(String icon) {
        return (icon == null || icon.isBlank()) ? "bi bi-circle" : icon;
    }
}

