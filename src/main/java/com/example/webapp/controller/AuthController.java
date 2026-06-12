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

/**
 * Controller de autenticação — gere o fluxo de login e logout.
 *
 * <h3>Rotas</h3>
 * <ul>
 *   <li>{@code GET  /login}  — apresenta o formulário de login</li>
 *   <li>{@code POST /login}  — valida credenciais contra a API e inicia sessão</li>
 *   <li>{@code POST /logout} — termina a sessão (PRG → redirige para /login)</li>
 * </ul>
 *
 * <h3>Nota PRG</h3>
 * <p>Em caso de credenciais inválidas, o POST /login re-renderiza o template
 * (sem redirect) para apresentar a mensagem de erro com o email pré-preenchido.
 * Esta é uma excepção documentada ao padrão PRG, aceitável para formulários de login.</p>
 */
@Controller
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private SessionService sessionService;

    // ──────────────────────────────────────────────────────────────────────────
    // GET /login
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Apresenta o formulário de login.
     * Se o utilizador já estiver autenticado, redirige para a área de cliente.
     */
    @GetMapping("/login")
    public String loginPage(HttpSession session, Model model) {
        if (sessionService.isAuthenticated(session)) {
            return "redirect:/client-area";
        }
        return "login";
    }

    // ──────────────────────────────────────────────────────────────────────────
    // POST /login
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Processa as credenciais submetidas pelo formulário de login.
     *
     * <p>Fluxo:</p>
     * <ol>
     *   <li>Chama a API de autenticação com as credenciais.</li>
     *   <li>Em caso de sucesso: constrói o {@link SessionUser}, guarda na sessão,
     *       redirige para o dashboard adequado ao role.</li>
     *   <li>Em caso de falha (credenciais inválidas ou erro de rede):
     *       re-renderiza o formulário com mensagem de erro e email pré-preenchido.</li>
     * </ol>
     */
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        try {
            LoginResponse resp = authService.login(email, password);

            if (resp == null) {
                // Credenciais inválidas (HTTP 4xx)
                log.warn("Tentativa de login falhada para: {}", email);
                model.addAttribute("error", "E-mail ou palavra-passe incorrectos. Por favor, tente novamente.");
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

    // ──────────────────────────────────────────────────────────────────────────
    // POST /logout
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Termina a sessão do utilizador e redirige para /login (PRG).
     */
    @PostMapping("/logout")
    public String logout(HttpSession session) {
        sessionService.clearUser(session);
        session.invalidate();
        return "redirect:/login";
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Helpers
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Mapeia o role do utilizador para a rota de dashboard correspondente.
     * Por omissão, redirige para /client-area (dashboard de cliente B2B).
     *
     * @param role role devolvido pela API (ex.: "CLIENTE", "ADMIN", "GESTOR")
     * @return string de redirect para a rota adequada
     */
    private String redirectByRole(String role) {
        if (role == null) {
            return "redirect:/client-area";
        }
        return switch (role.toUpperCase()) {
            case "CLIENTE" -> "redirect:/client-area";
            // Outros roles poderão ser adicionados aqui à medida que o portal evolua
            default -> "redirect:/client-area";
        };
    }
}
