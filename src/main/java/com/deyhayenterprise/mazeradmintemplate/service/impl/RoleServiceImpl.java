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
import com.deyhayenterprise.mazeradmintemplate.entity.Movtipo;
import com.deyhayenterprise.mazeradmintemplate.entity.Role;
import com.deyhayenterprise.mazeradmintemplate.repository.MenuOptionRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.MovtipoRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.RoleRepository;
import com.deyhayenterprise.mazeradmintemplate.service.RoleService;
import com.deyhayenterprise.mazeradmintemplate.service.dto.ModuleActionPermissionView;
import com.deyhayenterprise.mazeradmintemplate.service.dto.MenuOptionsGroupView;
import com.deyhayenterprise.mazeradmintemplate.service.dto.OptionPermissionView;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private static final String SUFFIX_AFECTAR = "_AFECTAR";
    private static final String SUFFIX_CANCELAR = "_CANCELAR";

    private final RoleRepository roleRepository;
    private final MenuOptionRepository menuOptionRepository;
    private final MovtipoRepository movtipoRepository;

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
    public Set<Long> findAssignedMovtipoIds(Long roleId) {
        return roleRepository.findById(roleId)
                .map(role -> role.getMovtipos().stream()
                        .map(Movtipo::getId)
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .orElseGet(LinkedHashSet::new);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> findAssignedAfectarOptionIds(Long roleId) {
        return findAssignedActionOptionIds(roleId, SUFFIX_AFECTAR);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Long> findAssignedCancelarOptionIds(Long roleId) {
        return findAssignedActionOptionIds(roleId, SUFFIX_CANCELAR);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuOptionsGroupView> buildPermissionCatalog() {
        Map<AppMenu, List<MenuOption>> grouped = menuOptionRepository.findAllActiveWithMenu().stream()
                .filter(option -> !isActionOption(option))
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
    @Transactional(readOnly = true)
    public List<ModuleActionPermissionView> buildActionPermissionCatalog() {
        Map<AppMenu, List<MenuOption>> grouped = menuOptionRepository.findAllActiveWithMenu().stream()
                .collect(Collectors.groupingBy(MenuOption::getMenu, LinkedHashMap::new, Collectors.toList()));

        return grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(Comparator.comparing(AppMenu::getOrdenVisual)))
                .map(entry -> {
                    AppMenu menu = entry.getKey();
                    String menuCodeUpper = menu.getCodigo().toUpperCase(Locale.ROOT);

                    MenuOption afectar = entry.getValue().stream()
                            .filter(opt -> (menuCodeUpper + SUFFIX_AFECTAR).equalsIgnoreCase(opt.getCodigo()))
                            .findFirst()
                            .orElse(null);

                    MenuOption cancelar = entry.getValue().stream()
                            .filter(opt -> (menuCodeUpper + SUFFIX_CANCELAR).equalsIgnoreCase(opt.getCodigo()))
                            .findFirst()
                            .orElse(null);

                    return new ModuleActionPermissionView(
                            menu.getId(),
                            menu.getCodigo(),
                            menu.getNombre(),
                            afectar != null ? afectar.getId() : null,
                            cancelar != null ? cancelar.getId() : null,
                            afectar != null,
                            cancelar != null
                    );
                })
                .toList();
    }

    @Override
    @Transactional
    public void updateRolePermissions(Long roleId, Set<Long> optionIds, Set<Long> movtipoIds) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado."));

        Set<MenuOption> options = optionIds == null
                ? new LinkedHashSet<>()
                : new LinkedHashSet<>(menuOptionRepository.findAllById(optionIds));

        Set<Movtipo> movtipos = movtipoIds == null
                ? new LinkedHashSet<>()
                : new LinkedHashSet<>(movtipoRepository.findAllById(movtipoIds));

        role.getOptions().clear();
        role.getOptions().addAll(options);
        role.getMovtipos().clear();
        role.getMovtipos().addAll(movtipos);
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

    private Set<Long> findAssignedActionOptionIds(Long roleId, String suffix) {
        return roleRepository.findById(roleId)
                .map(role -> role.getOptions().stream()
                        .filter(option -> option.getCodigo() != null
                                && option.getCodigo().toUpperCase(Locale.ROOT).endsWith(suffix))
                        .map(MenuOption::getId)
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                .orElseGet(LinkedHashSet::new);
    }

    private boolean isActionOption(MenuOption option) {
        if (option.getCodigo() == null) {
            return false;
        }
        String code = option.getCodigo().toUpperCase(Locale.ROOT);
        return code.endsWith(SUFFIX_AFECTAR) || code.endsWith(SUFFIX_CANCELAR);
    }
}

