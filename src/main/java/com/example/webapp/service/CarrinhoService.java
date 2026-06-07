package com.example.webapp.service;

import com.example.webapp.model.carrinho.Carrinho;
import com.example.webapp.model.carrinho.CarrinhoItem;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

/**
 * Serviço que gere o carrinho de compras armazenado na sessão HTTP.
 *
 * <p>Segue o mesmo padrão que {@link SessionService}: usa um atributo de sessão
 * com uma chave constante para guardar e recuperar o objecto {@link Carrinho}.</p>
 *
 * <h3>Exemplo de utilização num controller</h3>
 * <pre>{@code
 * @Autowired
 * private CarrinhoService carrinhoService;
 *
 * // Leitura
 * Carrinho carrinho = carrinhoService.getCarrinho(session);
 *
 * // Adicionar item
 * CarrinhoItem item = new CarrinhoItem(produtoId, produtoNome,
 *                                      palletTipoId, palletTipoNome,
 *                                      quantidadePallets);
 * carrinhoService.adicionarItem(session, item);
 * }</pre>
 */
@Service
public class CarrinhoService {

    /** Chave do atributo de sessão onde o carrinho é guardado. */
    private static final String SESSION_KEY = "carrinho";

    // ──────────────────────────────────────────────────────────────────────────
    // Métodos públicos
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Devolve o carrinho da sessão corrente.
     * Se ainda não existir nenhum carrinho na sessão, cria um vazio e guarda-o.
     *
     * @param session sessão HTTP corrente
     * @return carrinho existente ou novo carrinho vazio
     */
    public Carrinho getCarrinho(HttpSession session) {
        Carrinho carrinho = (Carrinho) session.getAttribute(SESSION_KEY);
        if (carrinho == null) {
            carrinho = new Carrinho();
            session.setAttribute(SESSION_KEY, carrinho);
        }
        return carrinho;
    }

    /**
     * Adiciona um item ao carrinho e guarda o estado actualizado na sessão.
     *
     * @param session sessão HTTP corrente
     * @param item    item a adicionar
     */
    public void adicionarItem(HttpSession session, CarrinhoItem item) {
        Carrinho carrinho = getCarrinho(session);
        carrinho.add(item);
        session.setAttribute(SESSION_KEY, carrinho);
    }

    /**
     * Remove o item na posição {@code index} do carrinho (0-based).
     *
     * @param session sessão HTTP corrente
     * @param index   índice do item a remover
     */
    public void removerItem(HttpSession session, int index) {
        Carrinho carrinho = getCarrinho(session);
        carrinho.remove(index);
        session.setAttribute(SESSION_KEY, carrinho);
    }

    /**
     * Remove todos os itens do carrinho.
     *
     * @param session sessão HTTP corrente
     */
    public void limparCarrinho(HttpSession session) {
        Carrinho carrinho = getCarrinho(session);
        carrinho.clear();
        session.setAttribute(SESSION_KEY, carrinho);
    }

    /**
     * Devolve o número total de itens no carrinho (para badge na navbar).
     *
     * @param session sessão HTTP corrente
     * @return número de itens no carrinho
     */
    public int contarItens(HttpSession session) {
        return getCarrinho(session).totalItens();
    }
}
