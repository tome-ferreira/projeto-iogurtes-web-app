package com.example.webapp.model.carrinho;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Carrinho implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<CarrinhoItem> items = new ArrayList<>();

    public void add(CarrinhoItem item) {
        if (item == null)
            throw new IllegalArgumentException("item não pode ser nulo");
        items.add(item);
    }

    public void remove(int index) {
        items.remove(index);
    }

    public void clear() {
        items.clear();
    }

    public List<CarrinhoItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public int totalItens() {
        return items.size();
    }

    public double getTotalCarrinho() {
        return items.stream()
                .mapToDouble(CarrinhoItem::getPrecoTotal)
                .sum();
    }
}
