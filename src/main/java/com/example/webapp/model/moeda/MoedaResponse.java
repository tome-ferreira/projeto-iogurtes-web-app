package com.example.webapp.model.moeda;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MoedaResponse {

    public String id;
    public String codigo;
    public String nome;
    public String simbolo;
    public BigDecimal taxaConversaoEur;
    public Boolean isActive;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;

}
