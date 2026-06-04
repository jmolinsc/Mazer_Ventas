package com.deyhayenterprise.mazeradmintemplate.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

import com.deyhayenterprise.mazeradmintemplate.entity.Cliente;
import com.deyhayenterprise.mazeradmintemplate.entity.Producto;
import com.deyhayenterprise.mazeradmintemplate.entity.Venta;
import com.deyhayenterprise.mazeradmintemplate.entity.VentaDetalle;
import com.deyhayenterprise.mazeradmintemplate.repository.ClienteRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.MenuOptionRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.ProductoRepository;
import com.deyhayenterprise.mazeradmintemplate.repository.VentaRepository;
import com.deyhayenterprise.mazeradmintemplate.service.ReporteService;
import com.deyhayenterprise.mazeradmintemplate.service.VentaService;
import com.deyhayenterprise.mazeradmintemplate.web.form.VentaCreateForm;
import com.deyhayenterprise.mazeradmintemplate.web.form.VentaDetalleForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class VentaServiceImpl implements VentaService {

    private static final Set<String> COMPORTAMIENTOS_SOPORTADOS =
            Set.of("FACTURA", "PEDIDO", "DEVOLUCION", "NOTA_CREDITO");
    private static final String ESTATUS_CONCLUIDO = "CONCLUIDO";
    private static final String ESTATUS_PENDIENTE = "PENDIENTE";
    private static final String ESTATUS_SIN_AFECTAR = "SINAFECTAR";
    private static final String ESTATUS_CANCELADO = "CANCELADO";
    private static final Set<String> ESTATUS_CON_CORRELATIVO =
            Set.of(ESTATUS_CONCLUIDO, ESTATUS_PENDIENTE);

    private final VentaRepository ventaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final MenuOptionRepository menuOptionRepository;
    private final ReporteService reporteService;

    @Override
    @Transactional(readOnly = true)
    public List<Venta> findAll() {
        return ventaRepository.findAllByOrderByFechaDescIdDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public Venta findById(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada."));

        // Inicializa relaciones que se usan en reportes/controladores fuera de la transaccion.
        if (venta.getCliente() != null) {
            venta.getCliente().getNombre();
        }
        if (venta.getDetalles() != null) {
            venta.getDetalles().forEach(det -> {
                det.getCantidad();
                if (det.getProducto() != null) {
                    det.getProducto().getNombre();
                }
            });
        }

        return venta;
    }

    @Override
    @Transactional
    public Venta executeTransaction(Long id, VentaCreateForm form, String accion, String comportamiento) {
        String comportamientoNormalizado = normalizeAndValidateComportamiento(comportamiento);
        String accionNormalizada = normalizeAccion(accion);

        return switch (comportamientoNormalizado) {
            case "FACTURA" -> processFactura(id, form, accionNormalizada, comportamientoNormalizado);
            case "PEDIDO" -> processPedido(id, form, accionNormalizada, comportamientoNormalizado);
            case "DEVOLUCION" -> processDevolucion(id, form, accionNormalizada, comportamientoNormalizado);
            case "NOTA_CREDITO" -> processNotaCredito(id, form, accionNormalizada, comportamientoNormalizado);
            default -> throw new IllegalArgumentException("Comportamiento no soportado: " + comportamientoNormalizado);
        };
    }

    private Venta processFactura(Long id, VentaCreateForm form, String accion, String comportamiento) {
        switch (accion) {
            case "GUARDAR":
                // TODO FACTURA: agrega validaciones/transformaciones previas a guardar.
                return saveOrUpdateWithStatus(id, form, comportamiento, null, false);
            case "AFECTAR":
                validateCurrentUserActionPermission("afectar", comportamiento);
                return processFacturaAfectar(id, form, comportamiento);
            case "IMPRIMIR":
                // TODO FACTURA: agrega lógica previa de impresión (folio, auditoría, etc.).
                return saveOrUpdateWithStatus(id, form, comportamiento, null, false);
            case "CANCELAR":
                validateCurrentUserActionPermission("cancelar", comportamiento);
                // TODO FACTURA: agrega reglas de cancelación.
                return saveOrUpdateWithStatus(id, form, comportamiento, ESTATUS_CANCELADO, false);
            default:
                throw new IllegalArgumentException("Accion no soportada para FACTURA: " + accion);
        }
    }

    private Venta processFacturaAfectar(Long id, VentaCreateForm form, String comportamiento) {
        validateFacturaAfectarInput(form);

        // Persistencia centralizada (crea/actualiza) con estatus concluido.
        Venta venta = saveOrUpdateWithStatus(id, form, comportamiento, ESTATUS_CONCLUIDO, true);

        // Hooks para tu lógica de negocio FACTURA/AFECTAR.
        runFacturaAfectarPostPersistence(venta, form);
        return venta;
    }

    private void validateFacturaAfectarInput(VentaCreateForm form) {
        if (!StringUtils.hasText(form.getMov())) {
            throw new IllegalArgumentException("Para afectar FACTURA debe seleccionar un tipo de movimiento.");
        }
    }

    private void runFacturaAfectarPostPersistence(Venta venta, VentaCreateForm form) {
        if (venta == null || form == null) {
            return;
        }
        // TODO FACTURA/AFECTAR #1: registrar movimiento de inventario/kardex.
        // TODO FACTURA/AFECTAR #2: generar cuenta por cobrar o actualizar saldo cliente.
        // TODO FACTURA/AFECTAR #3: generar asientos contables o evento de auditoría.
        // FACTURA/AFECTAR #4: generar PDF automático y guardarlo en disco.
        generarYGuardarPdfAutomatico(venta);
    }

    /**
     * Genera el PDF de la venta y lo guarda en uploads/reportes/{id}.pdf.
     * No lanza excepción para no interrumpir el flujo de negocio si falla.
     */
    private void generarYGuardarPdfAutomatico(Venta venta) {
        try {
            java.nio.file.Path carpeta = Paths.get("uploads", "reportes");
            if (!Files.exists(carpeta)) {
                Files.createDirectories(carpeta);
            }
            ByteArrayOutputStream pdf = reporteService.generarPDF(venta);
            String nombreArchivo = reporteService.obtenerNombreArchivo(venta);
            java.nio.file.Path archivo = carpeta.resolve(venta.getId() + "_" + nombreArchivo);
            Files.write(archivo, pdf.toByteArray(),
                    StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            log.info("PDF generado automáticamente para venta {}: {}", venta.getId(), archivo);
        } catch (IOException e) {
            log.error("No se pudo guardar el PDF automático para venta {}: {}", venta.getId(), e.getMessage());
        }
    }

    private Venta processPedido(Long id, VentaCreateForm form, String accion, String comportamiento) {
        switch (accion) {
            case "GUARDAR":
                // TODO PEDIDO: agrega validaciones de pedido (cupos, límites, etc.).
                return saveOrUpdateWithStatus(id, form, comportamiento, null, false);
            case "AFECTAR":
                validateCurrentUserActionPermission("afectar", comportamiento);
                // Regla solicitada: PEDIDO al afectar permanece en estatus pendiente y reserva inventario.
                return saveOrUpdateWithStatus(id, form, comportamiento, ESTATUS_PENDIENTE, true);
            case "IMPRIMIR":
                // TODO PEDIDO: agrega generación de formato de pedido.
                return saveOrUpdateWithStatus(id, form, comportamiento, null, false);
            case "CANCELAR":
                validateCurrentUserActionPermission("cancelar", comportamiento);
                // TODO PEDIDO: agrega reglas de reverso/cancelación.
                return saveOrUpdateWithStatus(id, form, comportamiento, ESTATUS_CANCELADO, false);
            default:
                throw new IllegalArgumentException("Accion no soportada para PEDIDO: " + accion);
        }
    }

    private Venta processDevolucion(Long id, VentaCreateForm form, String accion, String comportamiento) {
        switch (accion) {
            case "GUARDAR":
                // TODO DEVOLUCION: valida motivo y condiciones de devolución.
                return saveOrUpdateWithStatus(id, form, comportamiento, null, false);
            case "AFECTAR":
                validateCurrentUserActionPermission("afectar", comportamiento);
                // TODO DEVOLUCION: aplica entrada a inventario o ajuste correspondiente.
                return saveOrUpdateWithStatus(id, form, comportamiento, ESTATUS_CONCLUIDO, true);
            case "IMPRIMIR":
                // TODO DEVOLUCION: genera comprobante de devolución.
                return saveOrUpdateWithStatus(id, form, comportamiento, null, false);
            case "CANCELAR":
                validateCurrentUserActionPermission("cancelar", comportamiento);
                // TODO DEVOLUCION: revierte movimientos asociados.
                return saveOrUpdateWithStatus(id, form, comportamiento, ESTATUS_CANCELADO, false);
            default:
                throw new IllegalArgumentException("Accion no soportada para DEVOLUCION: " + accion);
        }
    }

    private Venta processNotaCredito(Long id, VentaCreateForm form, String accion, String comportamiento) {
        switch (accion) {
            case "GUARDAR":
                // TODO NOTA_CREDITO: valida documento origen y montos.
                return saveOrUpdateWithStatus(id, form, comportamiento, null, false);
            case "AFECTAR":
                validateCurrentUserActionPermission("afectar", comportamiento);
                // TODO NOTA_CREDITO: aplica impacto contable y de cartera.
                return saveOrUpdateWithStatus(id, form, comportamiento, ESTATUS_CONCLUIDO, true);
            case "IMPRIMIR":
                // TODO NOTA_CREDITO: genera impresión oficial.
                return saveOrUpdateWithStatus(id, form, comportamiento, null, false);
            case "CANCELAR":
                validateCurrentUserActionPermission("cancelar", comportamiento);
                // TODO NOTA_CREDITO: define reglas de anulación.
                return saveOrUpdateWithStatus(id, form, comportamiento, ESTATUS_CANCELADO, false);
            default:
                throw new IllegalArgumentException("Accion no soportada para NOTA_CREDITO: " + accion);
        }
    }

    private Venta saveOrUpdateWithStatus(Long id,
                                         VentaCreateForm form,
                                         String comportamiento,
                                         String estatusDestino,
                                         boolean afectarInventarioEnDestino) {
        Venta venta = saveOrUpdateInternal(id, form, comportamiento, estatusDestino, afectarInventarioEnDestino);
        // Si estatusDestino es null (accion GUARDAR), conserva el estatus actual de la venta.
        String finalEstatus = estatusDestino != null ? estatusDestino : venta.getEstado();
        if (finalEstatus == null) {
            finalEstatus = ESTATUS_SIN_AFECTAR;
        }
        venta.setEstado(finalEstatus);
        return ventaRepository.save(venta);
    }

    private Venta saveOrUpdateInternal(Long id,
                                       VentaCreateForm form,
                                       String comportamiento,
                                       String estatusDestino,
                                       boolean afectarInventarioEnDestino) {
        Cliente cliente = clienteRepository.findById(form.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));

        Venta venta;
        boolean afectabaInventarioAntes = false;
        if (id == null) {
            venta = new Venta();
            venta.setEstado(ESTATUS_SIN_AFECTAR);
        } else {
            venta = findById(id);
            afectabaInventarioAntes = impactsInventory(venta.getComportamiento(), venta.getEstado());
            if (afectabaInventarioAntes) {
                restoreStockFromExistingDetails(venta);
            }
            venta.getDetalles().clear();
        }

        venta.setCliente(cliente);
        venta.setFecha(form.getFecha());
        venta.setMov(form.getMov());
        if (!StringUtils.hasText(venta.getMovid()) && requiereCorrelativo(estatusDestino)) {
            venta.setMovid(generarCorrelativo(venta.getMov()));
        }
        venta.setComportamiento(comportamiento);

        applyDetalle(venta, form, afectarInventarioEnDestino);
        return ventaRepository.save(venta);
    }

    private boolean impactsInventory(String comportamiento, String estatus) {
        if (!StringUtils.hasText(comportamiento) || !StringUtils.hasText(estatus)) {
            return false;
        }

        String comp = comportamiento.trim().toUpperCase(Locale.ROOT);
        String sts = estatus.trim().toUpperCase(Locale.ROOT);

        if (ESTATUS_CONCLUIDO.equals(sts)) {
            return true;
        }
        return "PEDIDO".equals(comp) && ESTATUS_PENDIENTE.equals(sts);
    }

    private void validateCurrentUserActionPermission(String accion, String comportamiento) {
        String normalizedAction = StringUtils.hasText(accion) ? accion.trim().toLowerCase(Locale.ROOT) : "";
        String requiredCode = switch (normalizedAction) {
            case "afectar" -> "VENTAS_AFECTAR";
            case "cancelar" -> "VENTAS_CANCELAR";
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

    private void restoreStockFromExistingDetails(Venta venta) {
        for (VentaDetalle oldDetail : venta.getDetalles()) {
            Producto p = oldDetail.getProducto();
            p.setStock(p.getStock() + oldDetail.getCantidad());
            productoRepository.save(p);
        }
    }

    private boolean requiereCorrelativo(String estatusDestino) {
        if (!StringUtils.hasText(estatusDestino)) {
            return false;
        }
        return ESTATUS_CON_CORRELATIVO.contains(estatusDestino);
    }

    private String generarCorrelativo(String mov) {
        String movBase = StringUtils.hasText(mov) ? mov.trim() : "VTA";
        String prefijo = movBase.toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]", "");
        if (prefijo.isBlank()) {
            prefijo = "VTA";
        }
        long next = ventaRepository.countByMovIgnoreCase(movBase) + 1;
        return prefijo + "-" + String.format("%06d", next);
    }


    private String normalizeAccion(String accion) {
        if (!StringUtils.hasText(accion)) {
            throw new IllegalArgumentException("Debe indicar una accion para procesar la venta.");
        }
        return accion.trim().toUpperCase();
    }

    private String normalizeAndValidateComportamiento(String comportamiento) {
        if (!StringUtils.hasText(comportamiento)) {
            throw new IllegalArgumentException("Debe seleccionar un comportamiento para la venta.");
        }
        String normalized = comportamiento.trim().toUpperCase();
        if (!COMPORTAMIENTOS_SOPORTADOS.contains(normalized)) {
            throw new IllegalArgumentException("Comportamiento no soportado: " + normalized);
        }
        return normalized;
    }

    private void applyDetalle(Venta venta, VentaCreateForm form, boolean afectarInventario) {
        if (form.getDetalles() == null || form.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("Debe agregar al menos un producto al detalle.");
        }

        Map<Long, Integer> qtyByProduct = new LinkedHashMap<>();
        for (VentaDetalleForm d : form.getDetalles()) {
            if (d.getProductoId() == null || d.getCantidad() == null || d.getCantidad() < 1) {
                throw new IllegalArgumentException("Detalle de venta invalido.");
            }
            qtyByProduct.merge(d.getProductoId(), d.getCantidad(), Integer::sum);
        }

        Map<Long, Producto> products = new LinkedHashMap<>();
        for (Long productId : qtyByProduct.keySet()) {
            Producto producto = productoRepository.findByIdAndActivoTrue(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado en el detalle."));
            Integer qty = qtyByProduct.get(productId);
            if (afectarInventario && producto.getStock() < qty) {
                throw new IllegalArgumentException("Stock insuficiente para el producto: " + producto.getNombre());
            }
            products.put(productId, producto);
        }

        BigDecimal total = BigDecimal.ZERO;
        int totalCantidad = 0;
        Producto productoCabecera = null;

        for (Map.Entry<Long, Integer> e : qtyByProduct.entrySet()) {
            Producto producto = products.get(e.getKey());
            Integer qty = e.getValue();
            BigDecimal unit = producto.getPrecio();
            BigDecimal subtotal = unit.multiply(BigDecimal.valueOf(qty));

            if (productoCabecera == null) {
                productoCabecera = producto;
            }
            totalCantidad += qty;

            VentaDetalle det = new VentaDetalle();
            det.setVenta(venta);
            det.setProducto(producto);
            det.setCantidad(qty);
            det.setPrecioUnitario(unit);
            det.setSubtotal(subtotal);
            venta.getDetalles().add(det);

            total = total.add(subtotal);

            if (afectarInventario) {
                producto.setStock(producto.getStock() - qty);
                productoRepository.save(producto);
            }
        }

        venta.setTotal(total);
        // Compatibilidad con esquema legacy de tabla ventas
        venta.setProducto(productoCabecera);
        venta.setCantidad(totalCantidad);
    }
}
