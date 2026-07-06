package com.deyhayenterprise.mazeradmintemplate.config;

import com.deyhayenterprise.mazeradmintemplate.dto.EmpresaGlobal;
import com.deyhayenterprise.mazeradmintemplate.entity.Empresa;
import com.deyhayenterprise.mazeradmintemplate.repository.EmpresaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class EmpresaConfig {

    private final EmpresaRepository empresaRepository;

    /**
     * Bean global que mantiene los datos de la empresa
     * Se crea al iniciar la aplicación y se mantiene en memoria
     */
    @Bean
    @Scope("singleton") // Solo una instancia para toda la aplicación
    public EmpresaGlobal empresaGlobal() {
        log.info("🚀 Creando Bean EmpresaGlobal...");

        try {
            // Buscar la empresa en la base de datos
            Empresa empresa = empresaRepository.findFirstBy();

            if (empresa == null) {
                log.warn("⚠️ No se encontró empresa activa. Creando por defecto...");
                empresa = crearEmpresaPorDefecto();
            }

            // Convertir Entity a DTO
            EmpresaGlobal empresaGlobal = convertirAEmpresaGlobal(empresa);

            log.info("✅ Bean EmpresaGlobal creado exitosamente: {}", empresaGlobal.getNombre());
            log.info("   - RUC: {}", empresaGlobal.getNrc());
            log.info("   - Telefono: {}", empresaGlobal.getTelefono());

            return empresaGlobal;

        } catch (Exception e) {
            log.error("❌ Error al crear Bean EmpresaGlobal: {}", e.getMessage());
            // Crear empresa por defecto
            return crearEmpresaGlobalPorDefecto();
        }
    }

    /**
     * Crea una empresa por defecto en la base de datos
     */
    private Empresa crearEmpresaPorDefecto() {
        Empresa empresa = new Empresa();
        empresa.setNombre("Mi Empresa S.A.");
        empresa.setNit("123456789-0");
        empresa.setDireccion("Av. Principal #123, Ciudad");
        empresa.setTelefono("(+503) 1234-5678");
        empresa.setEmail("info@miempresa.com");


        empresa = empresaRepository.save(empresa);
        log.info("✅ Empresa por defecto creada con ID: {}", empresa.getId());
        return empresa;
    }

    /**
     * Crea un EmpresaGlobal por defecto (en caso de error)
     */
    private EmpresaGlobal crearEmpresaGlobalPorDefecto() {
        return EmpresaGlobal.builder()
                .id(1L)
                .nombre("Mi Empresa S.A.")
                .nit("123456789-0")
                .direccion("Av. Principal #123, Ciudad")
                .telefono("(+503) 1234-5678")
                .email("info@miempresa.com")
                .build();
    }

    /**
     * Convierte Entidad Empresa a DTO EmpresaGlobal
     */
    private EmpresaGlobal convertirAEmpresaGlobal(Empresa empresa) {
        return EmpresaGlobal.builder()
                .id(empresa.getId())
                .nombre(empresa.getNombre())
                .nit(empresa.getNit())
                .direccion(empresa.getDireccion())
                .telefono(empresa.getTelefono())
                .email(empresa.getEmail())
                /*.logo(empresa.getLogo())
                .ciudad(empresa.getCiudad())
                .pais(empresa.getPais())
                .slogan(empresa.getSlogan())
                .website(empresa.getWebsite())
                .activo(empresa.getActivo())*/
                .build();
    }
}