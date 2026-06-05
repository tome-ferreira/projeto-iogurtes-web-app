package com.example.webapp.model.catalogo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa um tipo de pallet devolvido pela API.
 *
 * <p>Endpoint: {@code GET /pallet-tipos?page=0&size=200}</p>
 *
 * <p>Os nomes dos campos correspondem exactamente às chaves JSON da resposta da API.
 * Schema: {@code PalletTipoResponse}.</p>
 */
public class PalletTipoResponse {

    /** UUID do tipo de pallet. */
    public String id;

    /** Nome do tipo de pallet. */
    public String nome;

    /** Capacidade máxima em quilogramas. */
    public BigDecimal capacidadeKg;

    /** Indica se o tipo de pallet está activo. */
    public Boolean isActive;

    /** Data e hora de criação. */
    public LocalDateTime createdAt;

    /** Data e hora da última actualização. */
    public LocalDateTime updatedAt;
}
