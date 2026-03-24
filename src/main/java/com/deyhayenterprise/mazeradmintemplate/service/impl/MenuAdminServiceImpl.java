package com.deyhayenterprise.mazeradmintemplate.service.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.AppMenu;
import com.deyhayenterprise.mazeradmintemplate.entity.MenuOption;
import com.deyhayenterprise.mazeradmintemplate.repository.AppMenuRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.MenuOptionRepository;
import com.deyhayenterprise.mazeradmintemplate.service.MenuAdminService;
import com.deyhayenterprise.mazeradmintemplate.web.form.AppMenuForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.MenuOptionForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuAdminServiceImpl implements MenuAdminService {

    private final AppMenuRepository appMenuRepository;
    private final MenuOptionRepository menuOptionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<AppMenu> findAllMenus() {
        return appMenuRepository.findAllByOrderByOrdenVisualAsc();
    }

    @Override
    @Transactional
    public AppMenu createMenu(AppMenuForm form) {
        String codigo = normalizeCode(form.getCodigo());
        if (appMenuRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new IllegalArgumentException("Ya existe un menú con ese código.");
        }

        AppMenu menu = new AppMenu();
        menu.setCodigo(codigo);
        menu.setNombre(form.getNombre().trim());
        menu.setSeccion(form.getSeccion().trim());
        menu.setIcono(normalizeNullable(form.getIcono()));
        menu.setOrdenVisual(form.getOrdenVisual());
        menu.setActivo(true);
        return appMenuRepository.save(menu);
    }

    @Override
    @Transactional
    public MenuOption createOption(MenuOptionForm form) {
        String codigo = normalizeCode(form.getCodigo());
        String url = form.getUrl().trim();

        if (menuOptionRepository.existsByCodigoIgnoreCase(codigo)) {
            throw new IllegalArgumentException("Ya existe una opción con ese código.");
        }
        if (menuOptionRepository.existsByUrlIgnoreCase(url)) {
            throw new IllegalArgumentException("Ya existe una opción con esa URL.");
        }

        AppMenu menu = appMenuRepository.findByIdAndActivoTrue(form.getMenuId())
                .orElseThrow(() -> new IllegalArgumentException("Menú no encontrado."));

        MenuOption option = new MenuOption();
        option.setCodigo(codigo);
        option.setNombre(form.getNombre().trim());
        option.setUrl(url);
        option.setDescripcion(normalizeNullable(form.getDescripcion()));
        option.setOrdenVisual(form.getOrdenVisual());
        option.setActivo(true);
        option.setMenu(menu);
        return menuOptionRepository.save(option);
    }

    private String normalizeCode(String rawCode) {
        return rawCode.trim()
                .replace('-', '_')
                .replace(' ', '_')
                .toUpperCase(Locale.ROOT);
    }

    private String normalizeNullable(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}

