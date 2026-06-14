package com.example.webapp.api;

import com.example.webapp.model.PaginatedResponse;
import com.example.webapp.model.catalogo.ProdutoCatalogoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IProdutoCatalogoApiService {

        @GET("produtos-finais/catalogo")
        Call<PaginatedResponse<ProdutoCatalogoResponse>> findAllCatalogo(
                        @Query("page") int page,
                        @Query("size") int size);

        @GET("produtos-finais/{id}")
        Call<ProdutoCatalogoResponse> findById(
                        @Path("id") String id);
}
