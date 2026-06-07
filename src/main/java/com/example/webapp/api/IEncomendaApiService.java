package com.example.webapp.api;

import com.example.webapp.model.encomenda.CreateEncomendaRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IEncomendaApiService {

    @POST("encomendas")
    Call<Object> criar(@Body CreateEncomendaRequest request);

}
