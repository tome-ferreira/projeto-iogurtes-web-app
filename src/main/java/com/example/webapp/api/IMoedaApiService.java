package com.example.webapp.api;

import com.example.webapp.model.moeda.MoedaResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface IMoedaApiService {

    @GET("moedas/codigo/{codigo}")
    Call<MoedaResponse> getByCodigo(@Path("codigo") String codigo);

}
