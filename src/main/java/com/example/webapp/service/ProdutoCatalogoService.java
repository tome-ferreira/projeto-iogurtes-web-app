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

@Service
public class ProdutoCatalogoService {

    private static final Logger log = LoggerFactory.getLogger(ProdutoCatalogoService.class);

    private final IProdutoCatalogoApiService api = RetrofitClient.getInstance()
            .getService(IProdutoCatalogoApiService.class);

    public PaginatedResponse<ProdutoCatalogoResponse> getCatalogo(int page, int size) {
        @SuppressWarnings("unchecked")
        final PaginatedResponse<ProdutoCatalogoResponse>[] result = new PaginatedResponse[1];
        final String[] errorMessage = { null };

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

    public ProdutoCatalogoResponse getById(String id) {
        final ProdutoCatalogoResponse[] result = { null };
        final String[] errorMessage = { null };

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

    public static class ProdutoCatalogoException extends RuntimeException {
        public ProdutoCatalogoException(String message) {
            super(message);
        }
    }

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
