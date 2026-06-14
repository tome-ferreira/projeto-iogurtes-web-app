package com.example.webapp.service;

import com.example.webapp.model.carrinho.Carrinho;
import com.example.webapp.model.carrinho.CarrinhoItem;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

@Service
public class CarrinhoService {

    private static final String SESSION_KEY = "carrinho";

    public Carrinho getCarrinho(HttpSession session) {
        Carrinho carrinho = (Carrinho) session.getAttribute(SESSION_KEY);
        if (carrinho == null) {
            carrinho = new Carrinho();
            session.setAttribute(SESSION_KEY, carrinho);
        }
        return carrinho;
    }

    public void adicionarItem(HttpSession session, CarrinhoItem item) {
        Carrinho carrinho = getCarrinho(session);
        carrinho.add(item);
        session.setAttribute(SESSION_KEY, carrinho);
    }

    public void removerItem(HttpSession session, int index) {
        Carrinho carrinho = getCarrinho(session);
        carrinho.remove(index);
        session.setAttribute(SESSION_KEY, carrinho);
    }

    public void limparCarrinho(HttpSession session) {
        Carrinho carrinho = getCarrinho(session);
        carrinho.clear();
        session.setAttribute(SESSION_KEY, carrinho);
    }

    public int contarItens(HttpSession session) {
        return getCarrinho(session).totalItens();
    }
}
