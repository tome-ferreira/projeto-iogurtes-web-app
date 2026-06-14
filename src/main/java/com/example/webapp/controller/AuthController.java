package com.example.webapp.controller;

import com.example.webapp.model.SessionUser;
import com.example.webapp.model.auth.LoginResponse;
import com.example.webapp.service.AuthService;
import com.example.webapp.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private SessionService sessionService;

    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        if (sessionService.isAuthenticated(session)) {
            return "redirect:/client-area";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        try {
            LoginResponse resp = authService.login(email, password);

            if (resp == null || resp.role == null || !"CLIENTE".equalsIgnoreCase(resp.role)) {
                // Credenciais inválidas (HTTP 4xx) ou acesso negado (role diferente de CLIENTE)
                log.warn("Tentativa de login falhada ou acesso não autorizado para: {}", email);
                model.addAttribute("error",
                        "E-mail ou palavra-passe incorrectos ou não tem permissões para aceder a esta área.");
                model.addAttribute("email", email);
                return "login";
            }

            // ── Criar SessionUser com token ──────────────────────────────────
            SessionUser user = new SessionUser(resp.id, resp.nome, resp.email, resp.role);
            user.setToken(resp.token);
            sessionService.setUser(session, user);

            log.info("Login bem-sucedido: user={}, role={}", resp.email, resp.role);

            // ── Redirigir para o dashboard adequado ao role ──────────────────
            return redirectByRole(resp.role);

        } catch (IOException e) {
            log.error("Erro de rede ao tentar autenticar: {}", e.getMessage(), e);
            model.addAttribute("error", "Erro de ligação ao servidor. Por favor, tente mais tarde.");
            model.addAttribute("email", email);
            return "login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        sessionService.clearUser(session);
        session.invalidate();
        return "redirect:/login";
    }

    private String redirectByRole(String role) {
        if (role == null) {
            return "redirect:/client-area";
        }
        return switch (role.toUpperCase()) {
            case "CLIENTE" -> "redirect:/client-area";
            default -> "redirect:/client-area";
        };
    }
}
