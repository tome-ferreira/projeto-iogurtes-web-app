package com.example.webapp.service;

import com.example.webapp.api.ApiQuery;
import com.example.webapp.api.IProdutoCatalogoApiService;
import com.example.webapp.api.RetrofitClient;
import com.example.webapp.model.PaginatedResponse;
import com.example.webapp.model.catalogo.ProdutoCatalogoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Serviço de aplicação que encapsula as chamadas HTTP ao endpoint de catálogo
 * de produtos finais ({@code GET /produtos-finais/catalogo}).
 *
 * <p>Utiliza {@link ApiQuery} para executar os pedidos Retrofit de forma
 * síncrona e encapsula erros de rede ou HTTP com mensagens legíveis.</p>
 *
 * <h3>Exemplo de utilização num controller</h3>
 * <pre>{@code
 * @Autowired
 * private ProdutoCatalogoService produtoCatalogoService;
 *
 * PaginatedResponse<ProdutoCatalogoResponse> page =
 *         produtoCatalogoService.getCatalogo(0, 10);
 * }</pre>
 */
@Service
public class ProdutoCatalogoService {

    private static final Logger log = LoggerFactory.getLogger(ProdutoCatalogoService.class);

    private final IProdutoCatalogoApiService api =
            RetrofitClient.getInstance().getService(IProdutoCatalogoApiService.class);

    /**
     * Devolve a página de produtos do catálogo.
     *
     * @param page  índice da página (0-based)
     * @param size  número de itens por página
     * @return página de produtos, ou uma resposta vazia em caso de erro
     * @throws ProdutoCatalogoException se ocorrer um erro ao aceder à API
     */
    public PaginatedResponse<ProdutoCatalogoResponse> getCatalogo(int page, int size) {
        @SuppressWarnings("unchecked")
        final PaginatedResponse<ProdutoCatalogoResponse>[] result = new PaginatedResponse[1];
        final String[] errorMessage = {null};

        ApiQuery.execute(api.findAllCatalogo(page, size), state -> {
            if (state.isSuccess()) {
                result[0] = state.getData();
            } else if (state.isError()) {
                errorMessage[0] = state.getErrorMessage();
                log.error("Erro ao obter catálogo de produtos: {}", state.getErrorMessage(),
                        state.getError());
            }
        });

        if (errorMessage[0] != null) {
            throw new ProdutoCatalogoException(errorMessage[0]);
        }

        return result[0];
    }

    /**
     * Devolve o detalhe completo de um produto final pelo seu UUID.
     *
     * @param id UUID do produto (formato string)
     * @return produto encontrado
     * @throws ProdutoCatalogoException se o produto não existir ou ocorrer erro de API
     */
    public ProdutoCatalogoResponse getById(String id) {
        final ProdutoCatalogoResponse[] result = {null};
        final String[] errorMessage = {null};

        ApiQuery.execute(api.findById(id), state -> {
            if (state.isSuccess()) {
                result[0] = state.getData();
            } else if (state.isError()) {
                errorMessage[0] = state.getErrorMessage();
                log.error("Erro ao obter produto {}: {}", id, state.getErrorMessage(),
                        state.getError());
            }
        });

        if (errorMessage[0] != null) {
            throw new ProdutoCatalogoException(errorMessage[0]);
        }

        return result[0];
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Excepção específica deste serviço
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Excepção lançada quando o pedido ao endpoint de catálogo falha.
     */
    public static class ProdutoCatalogoException extends RuntimeException {
        public ProdutoCatalogoException(String message) {
            super(message);
        }
    }

    /**
     * Cria uma {@link PaginatedResponse} vazia, para uso em estados de erro no controller.
     */
    public static PaginatedResponse<ProdutoCatalogoResponse> emptyResponse() {
        PaginatedResponse<ProdutoCatalogoResponse> empty = new PaginatedResponse<>();
        empty.content = Collections.emptyList();
        empty.totalElements = 0;
        empty.totalPages = 0;
        empty.number = 0;
        empty.size = 0;
        empty.first = true;
        empty.last = true;
        empty.empty = true;
        return empty;
    }
}
