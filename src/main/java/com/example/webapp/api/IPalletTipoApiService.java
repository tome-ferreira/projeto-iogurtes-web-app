package com.example.webapp.api;

import com.example.webapp.model.PaginatedResponse;
import com.example.webapp.model.catalogo.PalletTipoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IPalletTipoApiService {

    @GET("pallet-tipos")
    Call<PaginatedResponse<PalletTipoResponse>> findAll(
            @Query("page") int page,
            @Query("size") int size,
            @Query("sort") String sort,
            @Query("direction") String direction);
}
