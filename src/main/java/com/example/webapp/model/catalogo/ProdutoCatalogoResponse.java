package com.example.webapp.model.catalogo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProdutoCatalogoResponse {

    public String id;

    public String codigoSku;

    public String nome;

    public String descricao;

    public String abreviacaoSabor;

    public String estadoFisico;

    public Integer validadeDias;

    public BigDecimal precoVenda;

    public BigDecimal precoPorKg;

    public BigDecimal taxaIva;

    public Boolean visivelCliente;

    public Integer quantidadeLote;

    public List<ProdutoMateriaResponse> composicao;

    public Boolean isActive;

    public LocalDateTime createdAt;

    public LocalDateTime updatedAt;

    public static class ProdutoMateriaResponse {

        public String id;
        public String materiaId;
        public String materiaNome;
        public String materiaUnidade;
        public BigDecimal quantidadePorUnidadeProduto;
    }
}
