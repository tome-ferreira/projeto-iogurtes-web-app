package com.example.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller genérico para rotas públicas.
 *
 * <h3>Rotas</h3>
 * <ul>
 *   <li>{@code GET /} — página de landing (pública)</li>
 * </ul>
 *
 * <p>As rotas de autenticação (/login, /logout) estão em
 * {@link AuthController}.</p>
 */
@Controller
public class WebController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

}

