package com.example.webapp.service;

import com.example.webapp.api.IEncomendaApiService;
import com.example.webapp.api.RetrofitClient;
import com.example.webapp.model.encomenda.CreateEncomendaRequest;
import org.springframework.stereotype.Service;
import retrofit2.Response;

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
}
