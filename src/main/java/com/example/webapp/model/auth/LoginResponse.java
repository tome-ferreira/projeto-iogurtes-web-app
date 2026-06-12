package com.example.webapp.model.auth;

import java.util.UUID;

/**
 * Corpo da resposta de POST /auth/login.
 * Os nomes dos campos correspondem exactamente ao contrato da API.
 */
public class LoginResponse {

    public UUID   id;
    public String nome;
    public String email;
    public String role;
    public String token;
}
