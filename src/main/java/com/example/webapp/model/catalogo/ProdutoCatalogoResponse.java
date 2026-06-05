package com.example.webapp.model.catalogo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Representa um produto final devolvido pelo endpoint de catálogo da API.
 *
 * <p>Endpoint: {@code GET /produtos-finais/catalogo?page={page}&size={size}}</p>
 *
 * <p>Os nomes dos campos correspondem exactamente às chaves JSON da resposta da API.
 * Todos os campos são públicos para compatibilidade com o deserializador Gson
 * configurado no {@link com.example.webapp.api.RetrofitClient}.</p>
 */
public class ProdutoCatalogoResponse {

    /** UUID do produto. */
    public String id;

    /** Código SKU interno. */
    public String codigoSku;

    /** Nome comercial do produto. */
    public String nome;

    /** Descrição do produto. */
    public String descricao;

    /** Abreviação do sabor (ex.: "MOR", "BAN"). */
    public String abreviacaoSabor;

    /**
     * Estado físico do produto.
     * Valores possíveis da API: {@code "LIQUIDO"}, {@code "SOLIDO"}.
     */
    public String estadoFisico;

    /** Validade em dias. */
    public Integer validadeDias;

    /** Preço de venda unitário. */
    public BigDecimal precoVenda;

    /** Preço por quilograma. */
    public BigDecimal precoPorKg;

    /** Taxa de IVA aplicada (ex.: 0.06 para 6%). */
    public BigDecimal taxaIva;

    /** Indica se o produto está visível para clientes. */
    public Boolean visivelCliente;

    /** Quantidade por lote de produção. */
    public Integer quantidadeLote;

    /** Composição de matérias-primas do produto. */
    public List<ProdutoMateriaResponse> composicao;

    /** Indica se o produto está activo. */
    public Boolean isActive;

    /** Data e hora de criação. */
    public LocalDateTime createdAt;

    /** Data e hora da última actualização. */
    public LocalDateTime updatedAt;

    // ──────────────────────────────────────────────────────────────────────────
    // Sub-classe: composição de matéria-prima
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Item de composição de matéria-prima de um produto final.
     * Campos conforme o schema {@code ProdutoMateriaResponse} da API.
     */
    public static class ProdutoMateriaResponse {

        /** UUID da linha de composição. */
        public String id;

        /** UUID da matéria-prima. */
        public String materiaId;

        /** Nome da matéria-prima. */
        public String materiaNome;

        /** Unidade de medida da matéria-prima. */
        public String materiaUnidade;

        /** Quantidade desta matéria-prima por unidade de produto. */
        public BigDecimal quantidadePorUnidadeProduto;
    }
}
