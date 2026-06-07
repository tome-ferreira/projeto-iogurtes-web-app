package com.example.webapp.model.carrinho;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Carrinho de compras mantido em sessão HTTP.
 *
 * <p>Implementa {@link Serializable} para que o Spring Session possa
 * serializar o objecto correctamente quando necessário.</p>
 */
public class Carrinho implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<CarrinhoItem> items = new ArrayList<>();

    // ──────────────────────────────────────────────────────────────────────────
    // Operações
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Adiciona um item ao carrinho.
     *
     * @param item item a adicionar (não pode ser {@code null})
     */
    public void add(CarrinhoItem item) {
        if (item == null) throw new IllegalArgumentException("item não pode ser nulo");
        items.add(item);
    }

    /**
     * Remove o item na posição {@code index} (0-based).
     *
     * @param index índice do item a remover
     * @throws IndexOutOfBoundsException se o índice for inválido
     */
    public void remove(int index) {
        items.remove(index);
    }

    /**
     * Remove todos os itens do carrinho.
     */
    public void clear() {
        items.clear();
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Consulta
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Devolve uma vista imutável dos itens do carrinho.
     */
    public List<CarrinhoItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Indica se o carrinho está vazio.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Devolve o número total de itens no carrinho.
     */
    public int totalItens() {
        return items.size();
    }

    /**
     * Calcula o preço total de todos os itens no carrinho.
     * @return soma dos totais de cada item.
     */
    public double getTotalCarrinho() {
        return items.stream()
                .mapToDouble(CarrinhoItem::getPrecoTotal)
                .sum();
    }
}
