package com.example.webapp.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("enumDisplayHelper")
public class EnumDisplayHelper {

    private static final Map<String, String> ESTADO_FISICO = new HashMap<>();

    static {
        ESTADO_FISICO.put("LIQUIDO", "Líquido");
        ESTADO_FISICO.put("SOLIDO", "Sólido");
    }

    public String getEstadoFisicoLabel(String valor) {
        if (valor == null)
            return "—";
        return ESTADO_FISICO.getOrDefault(valor, valor);
    }

    private static final Map<String, String> ESTADO_ENCOMENDA = new HashMap<>();

    static {
        ESTADO_ENCOMENDA.put("PENDENTE", "Pendente");
        ESTADO_ENCOMENDA.put("EXPEDIDA", "Expedida");
        ESTADO_ENCOMENDA.put("CANCELADA", "Cancelada");
    }

    public String getEstadoEncomendaLabel(String valor) {
        if (valor == null)
            return "—";
        return ESTADO_ENCOMENDA.getOrDefault(valor, valor);
    }

    private static final Map<String, String> ESTADO_ENCOMENDA_MP = new HashMap<>();

    static {
        ESTADO_ENCOMENDA_MP.put("PENDENTE", "Pendente");
        ESTADO_ENCOMENDA_MP.put("ENCOMENDADA", "Encomendada");
        ESTADO_ENCOMENDA_MP.put("RECEBIDA", "Recebida");
        ESTADO_ENCOMENDA_MP.put("CANCELADA", "Cancelada");
    }

    public String getEstadoEncomendaMpLabel(String valor) {
        if (valor == null)
            return "—";
        return ESTADO_ENCOMENDA_MP.getOrDefault(valor, valor);
    }
}
