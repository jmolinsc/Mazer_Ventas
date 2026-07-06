package com.deyhayenterprise.mazeradmintemplate.config;

import com.deyhayenterprise.mazeradmintemplate.interceptor.EmpresaInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final EmpresaInterceptor empresaInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(empresaInterceptor)
                .addPathPatterns("/**") // Aplica a todas las rutas
                .excludePathPatterns(
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/fonts/**",
                        "/webjars/**",
                        "/api/**" // Excluye APIs REST
                );
    }
}