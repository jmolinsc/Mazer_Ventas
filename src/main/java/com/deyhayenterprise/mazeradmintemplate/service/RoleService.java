package com.deyhayenterprise.mazeradmintemplate.service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.deyhayenterprise.mazeradmintemplate.entity.AppMenu;
import com.deyhayenterprise.mazeradmintemplate.entity.MenuOption;
import com.deyhayenterprise.mazeradmintemplate.entity.Role;
import com.deyhayenterprise.mazeradmintemplate.repository.MenuOptionRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.RoleRepository;
import com.deyhayenterprise.mazeradmintemplate.service.dto.MenuOptionsGroupView;
import com.deyhayenterprise.mazeradmintemplate.service.dto.OptionPermissionView;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final MenuOptionRepository menuOptionRepository;

    @Transactional(readOnly = true)
    public List<Role> findAllRoles() {
        return roleRepository.findAllWithOptions();
    }

    @Transactional(readOnly = true)
    public Optional<Role> findRole(Long roleId) {
        return roleRepository.findById(roleId);
    }

    @Transactional(readOnly = true)
    public Set<Long> findAssignedOptionIds(Long roleId) {
        return roleRepository.findById(roleId)
                .map(role -> role.getOptions().stream()
                        .map(MenuOption::getId)
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .orElseGet(LinkedHashSet::new);
    }

    @Transactional(readOnly = true)
    public List<MenuOptionsGroupView> buildPermissionCatalog() {
        Map<AppMenu, List<MenuOption>> grouped = menuOptionRepository.findAllActiveWithMenu().stream()
                .collect(Collectors.groupingBy(MenuOption::getMenu, LinkedHashMap::new, Collectors.toList()));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(AppMenu::getOrdenVisual)))
                .map(entry -> new MenuOptionsGroupView(
                        entry.getKey().getId(),
                        entry.getKey().getNombre(),
                        entry.getKey().getSeccion(),
                        entry.getKey().getIcono(),
                        entry.getValue().stream()
                                .sorted(Comparator.comparing(MenuOption::getOrdenVisual))
                                .map(option -> new OptionPermissionView(option.getId(), option.getNombre(), option.getUrl(), option.getDescripcion()))
                                .toList()))
                .toList();
    }

    @Transactional
    public void updateRolePermissions(Long roleId, Set<Long> optionIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado."));

        Set<MenuOption> options = optionIds == null
                ? new LinkedHashSet<>()
                : new LinkedHashSet<>(menuOptionRepository.findAllById(optionIds));

        role.getOptions().clear();
        role.getOptions().addAll(options);
        roleRepository.save(role);
    }
}

