package com.example.webapp.service;

import com.example.webapp.model.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

/**
 * Gere a sessão HTTP do utilizador autenticado.
 *
 * <p>O utilizador é guardado na sessão com a chave {@code "loggedUser"}
 * após um login bem-sucedido. Todos os campos de {@link SessionUser}
 * (incluindo o token JWT) são persistidos na sessão.</p>
 *
 * <h3>Ciclo de vida</h3>
 * <ol>
 *   <li>Login: {@code setUser(session, user)} após validação com a API.</li>
 *   <li>Acesso: {@code getUser(session)} — devolve {@code null} se não autenticado.</li>
 *   <li>Verificação rápida: {@code isAuthenticated(session)}.</li>
 *   <li>Logout: {@code clearUser(session)} seguido de {@code session.invalidate()}.</li>
 * </ol>
 */
@Service
public class SessionService {

    private static final String SESSION_KEY = "loggedUser";

    /**
     * Devolve o utilizador activo da sessão HTTP, ou {@code null} se não existir.
     *
     * @param session sessão HTTP corrente
     * @return {@link SessionUser} autenticado, ou {@code null}
     */
    public SessionUser getUser(HttpSession session) {
        return (SessionUser) session.getAttribute(SESSION_KEY);
    }

    /**
     * Guarda o utilizador na sessão (usado após login bem-sucedido).
     * O token JWT é incluído em {@code user.getToken()}.
     *
     * @param session sessão HTTP corrente
     * @param user    utilizador autenticado com token preenchido
     */
    public void setUser(HttpSession session, SessionUser user) {
        session.setAttribute(SESSION_KEY, user);
    }

    /**
     * Remove o utilizador (e o token) da sessão (logout).
     *
     * @param session sessão HTTP corrente
     */
    public void clearUser(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
    }

    /**
     * Verifica se existe um utilizador autenticado na sessão.
     *
     * @param session sessão HTTP corrente
     * @return {@code true} se a sessão contém um utilizador, {@code false} caso contrário
     */
    public boolean isAuthenticated(HttpSession session) {
        return session.getAttribute(SESSION_KEY) != null;
    }
}