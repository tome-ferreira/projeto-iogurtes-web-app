package com.example.webapp.service;

import com.example.webapp.api.IMoedaApiService;
import com.example.webapp.api.RetrofitClient;
import com.example.webapp.model.moeda.MoedaResponse;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;

@Service
public class MoedaService {

    private final IMoedaApiService moedaApiService =
            RetrofitClient.getInstance().getService(IMoedaApiService.class);

    public MoedaResponse getByCodigo(String codigo) throws Exception {
        Response<MoedaResponse> response = moedaApiService.getByCodigo(codigo).execute();
        if (response.isSuccessful() && response.body() != null) {
            return response.body();
        } else {
            throw new Exception("Falha ao obter moeda por código: " + codigo + ". HTTP " + response.code());
        }
    }
}
