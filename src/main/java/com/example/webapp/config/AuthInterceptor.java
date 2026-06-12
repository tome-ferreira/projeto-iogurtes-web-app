package com.example.webapp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Interceptor de autenticação baseado em sessão HTTP.
 *
 * <p>Para cada pedido que não esteja excluído (via
 * {@link WebMvcConfig#addInterceptors}), verifica se existe um utilizador
 * autenticado na sessão HTTP. Se não existir, redirige para {@code /login}.</p>
 *
 * <h3>Abordagem</h3>
 * <p>Utiliza a Abordagem A (HandlerInterceptor), sem Spring Security.
 * A autenticação real é feita pela API de backend; a web app apenas
 * verifica a presença do utilizador na sessão.</p>
 *
 * <h3>Rotas públicas</h3>
 * <p>As exclusões estão configuradas em {@link WebMvcConfig}:
 * {@code /}, {@code /login}, {@code /css/**}, {@code /js/**},
 * {@code /images/**}, {@code /webjars/**}.</p>
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    private static final String SESSION_KEY = "loggedUser";

    /**
     * Intercepta o pedido antes de chegar ao controller.
     *
     * @return {@code true} se o pedido pode prosseguir;
     *         {@code false} se foi redirigido para /login
     */
    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws IOException {
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute(SESSION_KEY) != null) {
            return true; // Utilizador autenticado — prosseguir
        }

        log.debug("Acesso não autorizado a [{}] — a redirigir para /login",
                request.getRequestURI());
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }
}
