package com.example.webapp.api;

import com.example.webapp.model.encomenda.CreateEncomendaRequest;
import com.example.webapp.model.PaginatedResponse;
import com.example.webapp.model.encomenda.EncomendaResumoResponse;
import com.example.webapp.model.encomenda.EncomendaDetalheResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import okhttp3.ResponseBody;
import java.util.UUID;

public interface IEncomendaApiService {

    @POST("encomendas")
    Call<Object> criar(@Body CreateEncomendaRequest request);

    @GET("encomendas/user/{userId}")
    Call<PaginatedResponse<EncomendaResumoResponse>> getByUserId(
            @Path("userId") UUID userId,
            @Query("page") int page,
            @Query("size") int size
    );

    @GET("encomendas/{id}")
    Call<EncomendaDetalheResponse> getById(@Path("id") String id);

    @PATCH("encomendas/{id}/cancelar")
    Call<ResponseBody> cancelar(@Path("id") String id);

}
