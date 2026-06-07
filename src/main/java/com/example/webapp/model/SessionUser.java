package com.example.webapp.model;

public class SessionUser {
    private String id;
    private String nome;
    private String email;
    private String role;

    public SessionUser(String id, String nome, String email, String role) {
        this.id    = id;
        this.nome  = nome;
        this.email = email;
        this.role  = role;
    }

    // Getters
    public String getId()    { return id; }
    public String getNome()  { return nome; }
    public String getEmail() { return email; }
    public String getRole()  { return role; }
}