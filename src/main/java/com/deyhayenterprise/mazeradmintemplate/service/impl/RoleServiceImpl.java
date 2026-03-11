package com.deyhayenterprise.mazeradmintemplate.service.impl;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
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
import com.deyhayenterprise.mazeradmintemplate.service.RoleService;
import com.deyhayenterprise.mazeradmintemplate.service.dto.MenuOptionsGroupView;
import com.deyhayenterprise.mazeradmintemplate.service.dto.OptionPermissionView;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final MenuOptionRepository menuOptionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAllRoles() {
        return roleRepository.findAllWithOptions();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Role> findRole(Long roleId) {
        return roleRepository.findById(roleId);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> findAssignedOptionIds(Long roleId) {
        return roleRepository.findById(roleId)
                .map(role -> role.getOptions().stream()
                        .map(MenuOption::getId)
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .orElseGet(LinkedHashSet::new);
    }

    @Override
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

    @Override
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

    @Override
    @Transactional
    public Role createRole(String codigoInput, String nombreInput, String descripcionInput) {
        String codigo = normalizeCode(codigoInput);
        String nombre = nombreInput.trim();
        String descripcion = descripcionInput == null ? null : descripcionInput.trim();

        if (roleRepository.findByCodigoIgnoreCase(codigo).isPresent()) {
            throw new IllegalArgumentException("Ya existe un rol con ese codigo.");
        }

        Role role = new Role();
        role.setCodigo(codigo);
        role.setNombre(nombre);
        role.setDescripcion(descripcion == null || descripcion.isEmpty() ? null : descripcion);
        role.setActivo(true);

        return roleRepository.save(role);
    }

    private String normalizeCode(String rawCode) {
        return rawCode.trim()
                .replace('-', '_')
                .replace(' ', '_')
                .toUpperCase(Locale.ROOT);
    }
}

