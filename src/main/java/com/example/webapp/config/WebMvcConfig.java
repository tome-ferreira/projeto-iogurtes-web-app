package com.example.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/", // landing page
                        "/login", // formulário de login (GET + POST)
                        "/logout", // logout (POST) — o controller invalida a sessão
                        "/css/**", // ficheiros CSS
                        "/js/**", // ficheiros JavaScript
                        "/images/**", // imagens
                        "/webjars/**" // webjars
                );
    }
}
