package com.example.webapp.model.auth;

/**
 * Corpo do pedido POST /auth/login.
 * Os nomes dos campos correspondem exactamente ao contrato da API.
 */
public class LoginRequest {

    public String email;
    public String password;

    public LoginRequest(String email, String password) {
        this.email    = email;
        this.password = password;
    }
}
