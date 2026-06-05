package com.example.webapp.model;

import java.util.List;

/**
 * Wrapper genérico que mapeia a resposta paginada da API.
 *
 * <p>Os nomes dos campos coincidem exactamente com as chaves JSON
 * devolvidas pela API Spring (org.springframework.data.domain.Page):</p>
 * <ul>
 *   <li>{@code content}          — lista de itens da página corrente</li>
 *   <li>{@code totalElements}    — total de registos em todas as páginas</li>
 *   <li>{@code totalPages}       — número total de páginas</li>
 *   <li>{@code number}           — índice da página corrente (0-based)</li>
 *   <li>{@code size}             — dimensão da página</li>
 *   <li>{@code first}            — {@code true} se esta for a primeira página</li>
 *   <li>{@code last}             — {@code true} se esta for a última página</li>
 *   <li>{@code numberOfElements} — quantidade de itens nesta página</li>
 *   <li>{@code empty}            — {@code true} se {@code content} estiver vazio</li>
 * </ul>
 *
 * @param <T> tipo dos itens contidos em {@code content}
 */
public class PaginatedResponse<T> {

    /** Lista de itens na página corrente. */
    public List<T> content;

    /** Total de registos em todas as páginas. */
    public long totalElements;

    /** Total de páginas. */
    public int totalPages;

    /**
     * Índice da página corrente (0-based).
     * Corresponde ao campo {@code number} do Spring Page JSON.
     */
    public int number;

    /** Dimensão da página (registos por página). */
    public int size;

    /** {@code true} se esta for a primeira página. */
    public boolean first;

    /** {@code true} se esta for a última página. */
    public boolean last;

    /** Quantidade de itens nesta página. */
    public int numberOfElements;

    /** {@code true} se {@code content} estiver vazio. */
    public boolean empty;

    public PaginatedResponse() {}
}
