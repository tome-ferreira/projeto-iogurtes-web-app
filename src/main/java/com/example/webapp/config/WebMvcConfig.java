package com.example.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuração Spring MVC — regista o interceptor de autenticação.
 *
 * <h3>Rotas públicas (excluídas do interceptor)</h3>
 * <ul>
 *   <li>{@code /}         — landing page</li>
 *   <li>{@code /login}    — formulário de login (GET e POST)</li>
 *   <li>{@code /logout}   — endpoint de logout (POST)</li>
 *   <li>{@code /css/**}   — recursos estáticos CSS</li>
 *   <li>{@code /js/**}    — recursos estáticos JavaScript</li>
 *   <li>{@code /images/**} — imagens estáticas</li>
 *   <li>{@code /webjars/**} — webjars (se usados)</li>
 * </ul>
 *
 * <p>Todos os outros pedidos passam pelo {@link AuthInterceptor}.</p>
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/",           // landing page
                        "/login",      // formulário de login (GET + POST)
                        "/logout",     // logout (POST) — o controller invalida a sessão
                        "/css/**",     // ficheiros CSS
                        "/js/**",      // ficheiros JavaScript
                        "/images/**",  // imagens
                        "/webjars/**"  // webjars
                );
    }
}
