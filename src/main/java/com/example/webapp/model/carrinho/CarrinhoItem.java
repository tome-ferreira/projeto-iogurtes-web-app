package com.example.webapp.model.carrinho;

import java.util.UUID;

public class CarrinhoItem {

    private UUID produtoId;
    private String produtoNome;
    private UUID palletTipoId;
    private String palletTipoNome;
    private int quantidadePallets;
    private double precoUnitario;
    private double precoTotal;

    public CarrinhoItem() {
    }

    public CarrinhoItem(UUID produtoId, String produtoNome,
            UUID palletTipoId, String palletTipoNome,
            int quantidadePallets, double precoUnitario, double precoTotal) {
        this.produtoId = produtoId;
        this.produtoNome = produtoNome;
        this.palletTipoId = palletTipoId;
        this.palletTipoNome = palletTipoNome;
        this.quantidadePallets = quantidadePallets;
        this.precoUnitario = precoUnitario;
        this.precoTotal = precoTotal;
    }

    public UUID getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(UUID produtoId) {
        this.produtoId = produtoId;
    }

    public String getProdutoNome() {
        return produtoNome;
    }

    public void setProdutoNome(String produtoNome) {
        this.produtoNome = produtoNome;
    }

    public UUID getPalletTipoId() {
        return palletTipoId;
    }

    public void setPalletTipoId(UUID palletTipoId) {
        this.palletTipoId = palletTipoId;
    }

    public String getPalletTipoNome() {
        return palletTipoNome;
    }

    public void setPalletTipoNome(String palletTipoNome) {
        this.palletTipoNome = palletTipoNome;
    }

    public int getQuantidadePallets() {
        return quantidadePallets;
    }

    public void setQuantidadePallets(int quantidadePallets) {
        this.quantidadePallets = quantidadePallets;
    }

    public double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public double getPrecoTotal() {
        return precoTotal;
    }

    public void setPrecoTotal(double precoTotal) {
        this.precoTotal = precoTotal;
    }
}
