package com.example.webapp.service;

import com.example.webapp.api.ApiQuery;
import com.example.webapp.api.IPalletTipoApiService;
import com.example.webapp.api.RetrofitClient;
import com.example.webapp.model.PaginatedResponse;
import com.example.webapp.model.catalogo.PalletTipoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Serviço de aplicação que encapsula as chamadas HTTP ao endpoint de tipos de pallet
 * ({@code GET /pallet-tipos}).
 *
 * <p>Utiliza {@link ApiQuery} para executar os pedidos Retrofit de forma
 * síncrona e encapsula erros de rede ou HTTP com mensagens legíveis.</p>
 */
@Service
public class PalletTipoService {

    private static final Logger log = LoggerFactory.getLogger(PalletTipoService.class);

    private final IPalletTipoApiService api =
            RetrofitClient.getInstance().getService(IPalletTipoApiService.class);

    /**
     * Devolve todos os tipos de pallet activos (paginação generosa para obter tudo).
     *
     * @param page      índice da página (0-based)
     * @param size      número de itens por página
     * @param sort      campo de ordenação (ex.: "nome")
     * @param direction direcção de ordenação ("asc" ou "desc")
     * @return página de tipos de pallet, ou uma resposta vazia em caso de erro
     * @throws PalletTipoException se ocorrer um erro ao aceder à API
     */
    public PaginatedResponse<PalletTipoResponse> getAll(int page, int size,
                                                        String sort, String direction) {
        @SuppressWarnings("unchecked")
        final PaginatedResponse<PalletTipoResponse>[] result = new PaginatedResponse[1];
        final String[] errorMessage = {null};

        ApiQuery.execute(api.findAll(page, size, sort, direction), state -> {
            if (state.isSuccess()) {
                result[0] = state.getData();
            } else if (state.isError()) {
                errorMessage[0] = state.getErrorMessage();
                log.error("Erro ao obter tipos de pallet: {}", state.getErrorMessage(),
                        state.getError());
            }
        });

        if (errorMessage[0] != null) {
            throw new PalletTipoException(errorMessage[0]);
        }

        return result[0];
    }

    // ──────────────────────────────────────────────────────────────────────────
    // Excepção específica deste serviço
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Excepção lançada quando o pedido ao endpoint de tipos de pallet falha.
     */
    public static class PalletTipoException extends RuntimeException {
        public PalletTipoException(String message) {
            super(message);
        }
    }

    /**
     * Cria uma {@link PaginatedResponse} vazia, para uso em estados de erro no controller.
     */
    public static PaginatedResponse<PalletTipoResponse> emptyResponse() {
        PaginatedResponse<PalletTipoResponse> empty = new PaginatedResponse<>();
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
