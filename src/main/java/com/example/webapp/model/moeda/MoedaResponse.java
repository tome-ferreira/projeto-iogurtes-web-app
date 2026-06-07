package com.example.webapp.model.moeda;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa uma Moeda devolvida pela API.
 * Endpoint: GET /moedas/codigo/{codigo}
 */
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
