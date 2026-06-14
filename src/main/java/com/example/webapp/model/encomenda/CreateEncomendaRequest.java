package com.example.webapp.model.encomenda;

import java.util.List;

public class CreateEncomendaRequest {

    public String userId;
    public String moedaId;
    public List<EncomendaPalletItem> pallets;

    public CreateEncomendaRequest() {
    }

    public CreateEncomendaRequest(String userId, String moedaId, List<EncomendaPalletItem> pallets) {
        this.userId = userId;
        this.moedaId = moedaId;
        this.pallets = pallets;
    }
}
