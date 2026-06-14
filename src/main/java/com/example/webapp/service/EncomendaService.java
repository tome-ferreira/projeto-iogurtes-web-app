package com.example.webapp.service;

import com.example.webapp.api.IEncomendaApiService;
import com.example.webapp.api.RetrofitClient;
import com.example.webapp.model.encomenda.CreateEncomendaRequest;
import com.example.webapp.model.PaginatedResponse;
import com.example.webapp.model.encomenda.EncomendaResumoResponse;
import com.example.webapp.model.encomenda.EncomendaDetalheResponse;
import org.springframework.stereotype.Service;
import retrofit2.Response;
import java.util.UUID;

@Service
public class EncomendaService {

    private final IEncomendaApiService api =
            RetrofitClient.getInstance().getService(IEncomendaApiService.class);

    public void criar(CreateEncomendaRequest request) throws Exception {
        Response<Object> response = api.criar(request).execute();
        if (!response.isSuccessful()) {
            throw new Exception("Falha ao criar encomenda. HTTP " + response.code());
        }
    }

    public PaginatedResponse<EncomendaResumoResponse> getByUserId(UUID userId, int page, int size) throws Exception {
        Response<PaginatedResponse<EncomendaResumoResponse>> response = api.getByUserId(userId, page, size).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new Exception("Falha ao obter encomendas do utilizador. HTTP " + response.code());
        }
        return response.body();
    }

    public EncomendaDetalheResponse getById(String id) throws Exception {
        Response<EncomendaDetalheResponse> response = api.getById(id).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new Exception("Falha ao obter detalhe da encomenda. HTTP " + response.code());
        }
        return response.body();
    }

    public void cancelar(String id) throws Exception {
        Response<okhttp3.ResponseBody> response = api.cancelar(id).execute();
        if (!response.isSuccessful()) {
            throw new Exception("Falha ao cancelar a encomenda. HTTP " + response.code());
        }
    }
}
