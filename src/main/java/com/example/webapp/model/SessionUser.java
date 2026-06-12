package com.example.webapp.model;

import java.util.UUID;

public class SessionUser {
    private UUID id;
    private String nome;
    private String email;
    private String role;
    private String token;

    public SessionUser(UUID id, String nome, String email, String role) {
        this.id    = id;
        this.nome  = nome;
        this.email = email;
        this.role  = role;
    }

    // Getters
    public UUID getId()      { return id; }
    public String getNome()  { return nome; }
    public String getEmail() { return email; }
    public String getRole()  { return role; }
    public String getToken() { return token; }

    // Setter (token é definido após o login, separadamente)
    public void setToken(String token) { this.token = token; }
}