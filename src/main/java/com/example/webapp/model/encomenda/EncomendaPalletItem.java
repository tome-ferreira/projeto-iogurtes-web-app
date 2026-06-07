package com.example.webapp.model.encomenda;

/**
 * Representa um item de pallet numa encomenda.
 */
public class EncomendaPalletItem {

    public String produtoId;
    public String palletTipoId;
    public int quantidadePallets;

    public EncomendaPalletItem() {
    }

    public EncomendaPalletItem(String produtoId, String palletTipoId, int quantidadePallets) {
        this.produtoId = produtoId;
        this.palletTipoId = palletTipoId;
        this.quantidadePallets = quantidadePallets;
    }
}
