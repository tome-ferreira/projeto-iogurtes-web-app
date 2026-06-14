package com.example.webapp.service;

import com.example.webapp.model.SessionUser;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private static final String SESSION_KEY = "loggedUser";

    public SessionUser getUser(HttpSession session) {
        return (SessionUser) session.getAttribute(SESSION_KEY);
    }

    public void setUser(HttpSession session, SessionUser user) {
        session.setAttribute(SESSION_KEY, user);
    }

    public void clearUser(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
    }

    public boolean isAuthenticated(HttpSession session) {
        return session.getAttribute(SESSION_KEY) != null;
    }
}
