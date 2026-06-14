package com.example.webapp.model.encomenda;

import java.util.List;

public class EncomendaResumoResponse {
    public String id;
    public String userId;
    public String userNome;
    public String moedaId;
    public String moedaCodigo;
    public String moedaSimbolo;
    public Double taxaConversaoSnapshot;
    public String estado;
    public String dataEncomenda;
    public Double totalPreco;
    public Double totalPrecoEur;
    public List<EncomendaPalletResponse> pallets;
    public Boolean isActive;
    public String createdAt;
    public String updatedAt;
}
