package com.example.webapp.model.carrinho;

import java.util.UUID;

/**
 * Representa um item no carrinho de compras.
 *
 * <p>Cada item corresponde a uma combinação de produto final + tipo de pallet
 * com a quantidade desejada de pallets.</p>
 */
public class CarrinhoItem {

    /** UUID do produto final. */
    private UUID produtoId;

    /** Nome comercial do produto (para apresentação). */
    private String produtoNome;

    /** UUID do tipo de pallet seleccionado. */
    private UUID palletTipoId;

    /** Nome do tipo de pallet (para apresentação — não vai à API). */
    private String palletTipoNome;

    /** Número de pallets pretendidos. */
    private int quantidadePallets;

    /** Preço por unidade base (kg) no momento em que foi adicionado. */
    private double precoUnitario;

    /** Preço total desta linha (quantidadePallets * capacidadePallet * precoUnitario). */
    private double precoTotal;

    // ──────────────────────────────────────────────────────────────────────────
    // Construtores
    // ──────────────────────────────────────────────────────────────────────────

    public CarrinhoItem() {}

    public CarrinhoItem(UUID produtoId, String produtoNome,
                        UUID palletTipoId, String palletTipoNome,
                        int quantidadePallets, double precoUnitario, double precoTotal) {
        this.produtoId         = produtoId;
        this.produtoNome       = produtoNome;
        this.palletTipoId      = palletTipoId;
        this.palletTipoNome    = palletTipoNome;
        this.quantidadePallets = quantidadePallets;
        this.precoUnitario     = precoUnitario;
        this.precoTotal        = precoTotal;
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Getters e Setters
    // ──────────────────────────────────────────────────────────────────────────

    public UUID getProdutoId()                  { return produtoId; }
    public void setProdutoId(UUID produtoId)    { this.produtoId = produtoId; }

    public String getProdutoNome()                  { return produtoNome; }
    public void setProdutoNome(String produtoNome)  { this.produtoNome = produtoNome; }

    public UUID getPalletTipoId()                   { return palletTipoId; }
    public void setPalletTipoId(UUID palletTipoId)  { this.palletTipoId = palletTipoId; }

    public String getPalletTipoNome()                   { return palletTipoNome; }
    public void setPalletTipoNome(String palletTipoNome){ this.palletTipoNome = palletTipoNome; }

    public int getQuantidadePallets()                       { return quantidadePallets; }
    public void setQuantidadePallets(int quantidadePallets) { this.quantidadePallets = quantidadePallets; }

    public double getPrecoUnitario()                  { return precoUnitario; }
    public void setPrecoUnitario(double precoUnitario){ this.precoUnitario = precoUnitario; }

    public double getPrecoTotal()               { return precoTotal; }
    public void setPrecoTotal(double precoTotal){ this.precoTotal = precoTotal; }
}
