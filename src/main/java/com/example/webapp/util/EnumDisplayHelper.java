package com.example.webapp.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Utilitário Spring para converter valores de enum da API em texto legível em
 * Português Europeu, para exibição nos templates Thymeleaf.
 *
 * <h3>Como usar no Thymeleaf</h3>
 * <pre>{@code
 * <!-- Estado Físico -->
 * <span th:text="${@enumDisplayHelper.getEstadoFisicoLabel(produto.estadoFisico)}"></span>
 *
 * <!-- Estado Encomenda -->
 * <span th:text="${@enumDisplayHelper.getEstadoEncomendaLabel(encomenda.estado)}"></span>
 * }</pre>
 *
 * <p>Os mapeamentos são idênticos aos definidos em
 * {@code com.gestaoiogurtes.utils.EnumDisplayHelper} na desktop app.</p>
 */
@Component("enumDisplayHelper")
public class EnumDisplayHelper {

    // ── Estado Físico (ProdutoFinal) ─────────────────────────────────────────

    private static final Map<String, String> ESTADO_FISICO = new HashMap<>();

    static {
        ESTADO_FISICO.put("LIQUIDO", "Líquido");
        ESTADO_FISICO.put("SOLIDO",  "Sólido");
    }

    /**
     * Devolve o rótulo em Português para um valor do enum EstadoFisico.
     *
     * @param valor valor da API (ex: {@code "LIQUIDO"})
     * @return rótulo legível (ex: {@code "Líquido"}), ou {@code "—"} se null,
     *         ou o próprio valor se não mapeado
     */
    public String getEstadoFisicoLabel(String valor) {
        if (valor == null) return "—";
        return ESTADO_FISICO.getOrDefault(valor, valor);
    }

    // ── Estado Encomenda ─────────────────────────────────────────────────────

    private static final Map<String, String> ESTADO_ENCOMENDA = new HashMap<>();

    static {
        ESTADO_ENCOMENDA.put("PENDENTE",  "Pendente");
        ESTADO_ENCOMENDA.put("EXPEDIDA",  "Expedida");
        ESTADO_ENCOMENDA.put("CANCELADA", "Cancelada");
    }

    /**
     * Devolve o rótulo em Português para um valor do enum EstadoEncomenda.
     *
     * @param valor valor da API (ex: {@code "PENDENTE"})
     * @return rótulo legível, ou {@code "—"} se null, ou o próprio valor se não mapeado
     */
    public String getEstadoEncomendaLabel(String valor) {
        if (valor == null) return "—";
        return ESTADO_ENCOMENDA.getOrDefault(valor, valor);
    }

    // ── Estado Encomenda MP ──────────────────────────────────────────────────

    private static final Map<String, String> ESTADO_ENCOMENDA_MP = new HashMap<>();

    static {
        ESTADO_ENCOMENDA_MP.put("PENDENTE",    "Pendente");
        ESTADO_ENCOMENDA_MP.put("ENCOMENDADA", "Encomendada");
        ESTADO_ENCOMENDA_MP.put("RECEBIDA",    "Recebida");
        ESTADO_ENCOMENDA_MP.put("CANCELADA",   "Cancelada");
    }

    /**
     * Devolve o rótulo em Português para um valor do enum EstadoEncomendaMP.
     *
     * @param valor valor da API (ex: {@code "ENCOMENDADA"})
     * @return rótulo legível, ou {@code "—"} se null, ou o próprio valor se não mapeado
     */
    public String getEstadoEncomendaMpLabel(String valor) {
        if (valor == null) return "—";
        return ESTADO_ENCOMENDA_MP.getOrDefault(valor, valor);
    }
}
