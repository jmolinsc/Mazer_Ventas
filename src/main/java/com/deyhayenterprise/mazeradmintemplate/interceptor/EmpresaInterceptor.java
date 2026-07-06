package com.deyhayenterprise.mazeradmintemplate.interceptor;

import com.deyhayenterprise.mazeradmintemplate.dto.EmpresaGlobal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmpresaInterceptor implements HandlerInterceptor {

    private final EmpresaGlobal empresaGlobal;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object handler, ModelAndView modelAndView) {

        // Solo agregar si hay una vista (no para APIs REST)
        if (modelAndView != null && !isApiRequest(request)) {

            // 🔥 Inyectar el Bean en todas las vistas
            modelAndView.addObject("empresa", empresaGlobal);
            modelAndView.addObject("nombreEmpresa", empresaGlobal.getNombre());
            modelAndView.addObject("nitEmpresa", empresaGlobal.getNrc());
            modelAndView.addObject("direccionEmpresa", empresaGlobal.getDireccionCompleta());
            modelAndView.addObject("telefonoEmpresa", empresaGlobal.getTelefono());
            modelAndView.addObject("emailEmpresa", empresaGlobal.getEmail());

            log.debug("📋 Empresa inyectada en vista: {}", modelAndView.getViewName());
        }
    }

    /**
     * Detecta si es una petición API (para no inyectar en respuestas JSON)
     */
    private boolean isApiRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/") || path.startsWith("/v1/");
    }
}