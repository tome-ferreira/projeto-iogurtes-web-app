package com.gestaoiogurtes.webapp.service;

import com.gestaoiogurtes.webapp.model.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private static final String SESSION_KEY = "loggedUser";

    // ── MOCK — substitui pelo login real depois ──────────────────
    private static final SessionUser MOCK_USER = new SessionUser(
        "550830fa-e7c3-4e89-af34-9829bdd1349d",
        "António Silva",
        "antonio.silva@empresa.com",
        "ADMIN"
    );

    /**
     * Devolve o utilizador da sessão HTTP.
     * Em mock: ignora a sessão e devolve sempre o MOCK_USER.
     * Em produção: lê da sessão HTTP (ver guia abaixo).
     */
    public SessionUser getUser(HttpSession session) {
        // TODO: substituir por -> return (SessionUser) session.getAttribute(SESSION_KEY);
        return MOCK_USER;
    }

    /**
     * Guarda o utilizador na sessão (usado pelo login real).
     */
    public void setUser(HttpSession session, SessionUser user) {
        session.setAttribute(SESSION_KEY, user);
    }

    /**
     * Remove o utilizador da sessão (logout).
     */
    public void clearUser(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
    }
}