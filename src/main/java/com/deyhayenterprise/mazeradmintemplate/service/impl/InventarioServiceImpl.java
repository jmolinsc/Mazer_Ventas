package com.deyhayenterprise.mazeradmintemplate.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.deyhayenterprise.mazeradmintemplate.entity.Inventario;
import com.deyhayenterprise.mazeradmintemplate.entity.InventarioDetalle;
import com.deyhayenterprise.mazeradmintemplate.entity.Producto;
import com.deyhayenterprise.mazeradmintemplate.repository.InventarioRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.MenuOptionRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.ProductoRepository;
import com.deyhayenterprise.mazeradmintemplate.service.InventarioService;
import com.deyhayenterprise.mazeradmintemplate.web.form.InventarioCreateForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.InventarioDetalleForm;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventarioServiceImpl implements InventarioService {

    private static final Set<String> COMPORTAMIENTOS_SOPORTADOS = Set.of("ENTRADA", "SALIDA", "AJUSTE");
    private static final String ESTATUS_CONCLUIDO = "CONCLUIDO";
    private static final String ESTATUS_SIN_AFECTAR = "SINAFECTAR";
    private static final String ESTATUS_CANCELADO = "CANCELADO";
    private static final Set<String> ESTATUS_CON_CORRELATIVO = Set.of(ESTATUS_CONCLUIDO);

    private final InventarioRepository inventarioRepository;
    private final ProductoRepository productoRepository;
    private final MenuOptionRepository menuOptionRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Inventario> findAll() {
        return inventarioRepository.findAllByOrderByFechaDescIdDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Inventario findById(Long id) {
        return inventarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Movimiento de inventario no encontrado."));
    }

    @Override
    @Transactional
    public Inventario executeTransaction(Long id, InventarioCreateForm form, String accion, String comportamiento) {
        String comportamientoNormalizado = normalizeAndValidateComportamiento(comportamiento);
        String accionNormalizada = normalizeAccion(accion);

        return switch (accionNormalizada) {
            case "GUARDAR" -> saveOrUpdateWithStatus(id, form, comportamientoNormalizado, ESTATUS_SIN_AFECTAR, false);
            case "AFECTAR" -> {
                validateCurrentUserActionPermission("afectar", comportamientoNormalizado);
                validateAfectarInput(form);
                yield saveOrUpdateWithStatus(id, form, comportamientoNormalizado, ESTATUS_CONCLUIDO, true);
            }
            case "IMPRIMIR" -> saveOrUpdateWithStatus(id, form, comportamientoNormalizado,
                    resolveStatusForPrint(id), false);
            case "CANCELAR" -> {
                validateCurrentUserActionPermission("cancelar", comportamientoNormalizado);
                yield saveOrUpdateWithStatus(id, form, comportamientoNormalizado, ESTATUS_CANCELADO, false);
            }
            default -> throw new IllegalArgumentException("Accion no soportada para inventario: " + accionNormalizada);
        };
    }

    private Inventario saveOrUpdateWithStatus(Long id,
                                              InventarioCreateForm form,
                                              String comportamiento,
                                              String estatusDestino,
                                              boolean afectarInventarioEnDestino) {
        Inventario inventario = saveOrUpdateInternal(id, form, comportamiento, estatusDestino, afectarInventarioEnDestino);
        inventario.setEstado(estatusDestino);
        inventario.setEstatus(estatusDestino);
        return inventarioRepository.save(inventario);
    }

    private Inventario saveOrUpdateInternal(Long id,
                                            InventarioCreateForm form,
                                            String comportamiento,
                                            String estatusDestino,
                                            boolean afectarInventarioEnDestino) {
        Inventario inventario;
        boolean afectabaInventarioAntes = false;
        if (id == null) {
            inventario = new Inventario();
            inventario.setEstado(ESTATUS_SIN_AFECTAR);
        } else {
            inventario = findById(id);
            afectabaInventarioAntes = impactsInventory(inventario.getEstado());
            if (afectabaInventarioAntes) {
                restoreStockFromExistingDetails(inventario);
            }
            inventario.getDetalles().clear();
        }

        inventario.setFecha(form.getFecha());
        inventario.setMov(form.getMov());
        if (!StringUtils.hasText(inventario.getMovid()) && requiereCorrelativo(estatusDestino)) {
            inventario.setMovid(generarCorrelativo(inventario.getMov()));
        }
        inventario.setComportamiento(comportamiento);

        applyDetalle(inventario, form, comportamiento, afectarInventarioEnDestino);
        return inventarioRepository.save(inventario);
    }

    private void applyDetalle(Inventario inventario,
                              InventarioCreateForm form,
                              String comportamiento,
                              boolean afectarInventario) {
        if (form.getDetalles() == null || form.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("Debe agregar al menos un producto al detalle.");
        }

        Map<Long, Integer> qtyByProduct = new LinkedHashMap<>();
        for (InventarioDetalleForm d : form.getDetalles()) {
            if (d.getProductoId() == null || d.getCantidad() == null || d.getCantidad() < 1) {
                throw new IllegalArgumentException("Detalle de inventario invalido.");
            }
            qtyByProduct.merge(d.getProductoId(), d.getCantidad(), Integer::sum);
        }

        Map<Long, Producto> products = new LinkedHashMap<>();
        for (Long productId : qtyByProduct.keySet()) {
            Producto producto = productoRepository.findByIdAndActivoTrue(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado en el detalle."));

            Integer qty = qtyByProduct.get(productId);
            if (afectarInventario && "SALIDA".equals(comportamiento) && producto.getStock() < qty) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
            }
            products.put(productId, producto);
        }

        int totalCantidad = 0;
        for (Map.Entry<Long, Integer> e : qtyByProduct.entrySet()) {
            Producto producto = products.get(e.getKey());
            Integer qty = e.getValue();

            InventarioDetalle det = new InventarioDetalle();
            det.setInventario(inventario);
            det.setProducto(producto);
            det.setCantidad(qty);
            inventario.getDetalles().add(det);

            totalCantidad += qty;

            if (afectarInventario) {
                applyStockImpact(producto, qty, comportamiento, false);
                productoRepository.save(producto);
            }
        }

        inventario.setTotalCantidad(totalCantidad);
    }

    private void restoreStockFromExistingDetails(Inventario inventario) {
        String comportamiento = inventario.getComportamiento() == null
                ? ""
                : inventario.getComportamiento().trim().toUpperCase(Locale.ROOT);

        for (InventarioDetalle oldDetail : inventario.getDetalles()) {
            Producto p = oldDetail.getProducto();
            applyStockImpact(p, oldDetail.getCantidad(), comportamiento, true);
            productoRepository.save(p);
        }
    }

    private void applyStockImpact(Producto producto, int cantidad, String comportamiento, boolean reverse) {
        int delta = reverse ? -cantidad : cantidad;

        switch (comportamiento) {
            case "ENTRADA":
            case "AJUSTE":
                producto.setStock(producto.getStock() + delta);
                break;
            case "SALIDA":
                producto.setStock(producto.getStock() - delta);
                break;
            default:
                throw new IllegalArgumentException("Comportamiento no soportado para inventario: " + comportamiento);
        }

        if (producto.getStock() < 0) {
            throw new IllegalArgumentException("Stock negativo no permitido para producto: " + producto.getNombre());
        }
    }

    private boolean impactsInventory(String estatus) {
        if (!StringUtils.hasText(estatus)) {
            return false;
        }
        return ESTATUS_CONCLUIDO.equals(estatus.trim().toUpperCase(Locale.ROOT));
    }

    private String resolveStatusForPrint(Long id) {
        if (id == null) {
            return ESTATUS_SIN_AFECTAR;
        }
        Inventario inventario = findById(id);
        if (StringUtils.hasText(inventario.getEstatus())) {
            return inventario.getEstatus().trim().toUpperCase(Locale.ROOT);
        }
        if (StringUtils.hasText(inventario.getEstado())) {
            return inventario.getEstado().trim().toUpperCase(Locale.ROOT);
        }
        return ESTATUS_SIN_AFECTAR;
    }

    private boolean requiereCorrelativo(String estatusDestino) {
        return ESTATUS_CON_CORRELATIVO.contains(estatusDestino);
    }

    private String generarCorrelativo(String mov) {
        String movBase = StringUtils.hasText(mov) ? mov.trim() : "INV";
        String prefijo = movBase.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
        if (prefijo.isBlank()) {
            prefijo = "INV";
        }
        long next = inventarioRepository.countByMovIgnoreCase(movBase) + 1;
        return prefijo + "-" + String.format("%06d", next);
    }

    private String normalizeAccion(String accion) {
        if (!StringUtils.hasText(accion)) {
            throw new IllegalArgumentException("Debe indicar una accion para procesar inventario.");
        }
        return accion.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeAndValidateComportamiento(String comportamiento) {
        if (!StringUtils.hasText(comportamiento)) {
            throw new IllegalArgumentException("Debe seleccionar un comportamiento para inventario.");
        }
        String normalized = comportamiento.trim().toUpperCase(Locale.ROOT);
        if (!COMPORTAMIENTOS_SOPORTADOS.contains(normalized)) {
            throw new IllegalArgumentException("Comportamiento no soportado: " + normalized);
        }
        return normalized;
    }

    private void validateAfectarInput(InventarioCreateForm form) {
        if (!StringUtils.hasText(form.getMov())) {
            throw new IllegalArgumentException("Para afectar inventario debe seleccionar un tipo de movimiento.");
        }
    }

    private void validateCurrentUserActionPermission(String accion, String comportamiento) {
        String normalizedAction = StringUtils.hasText(accion) ? accion.trim().toLowerCase(Locale.ROOT) : "";
        String requiredCode = switch (normalizedAction) {
            case "afectar" -> "INVENTARIO_AFECTAR";
            case "cancelar" -> "INVENTARIO_CANCELAR";
            default -> null;
        };

        if (requiredCode == null) {
            return;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("No autenticado para ejecutar la accion: " + normalizedAction + ".");
        }

        Set<String> allowedCodes = menuOptionRepository.findAllowedCodesByUsername(authentication.getName()).stream()
                .map(code -> code == null ? "" : code.trim().toUpperCase(Locale.ROOT))
                .collect(Collectors.toSet());

        if (!allowedCodes.contains(requiredCode)) {
            throw new IllegalArgumentException(
                    "No tienes permiso para " + normalizedAction + " en comportamiento " + comportamiento + ".");
        }
    }
}


