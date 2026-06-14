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

@Service
public class PalletTipoService {

    private static final Logger log = LoggerFactory.getLogger(PalletTipoService.class);

    private final IPalletTipoApiService api = RetrofitClient.getInstance().getService(IPalletTipoApiService.class);

    public PaginatedResponse<PalletTipoResponse> getAll(int page, int size,
            String sort, String direction) {
        @SuppressWarnings("unchecked")
        final PaginatedResponse<PalletTipoResponse>[] result = new PaginatedResponse[1];
        final String[] errorMessage = { null };

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

    public static class PalletTipoException extends RuntimeException {
        public PalletTipoException(String message) {
            super(message);
        }
    }

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
