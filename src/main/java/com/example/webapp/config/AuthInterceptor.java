package com.example.webapp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    private static final String SESSION_KEY = "loggedUser";

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
