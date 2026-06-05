package com.example.webapp.api;

import com.example.webapp.model.PaginatedResponse;
import com.example.webapp.model.catalogo.PalletTipoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface Retrofit para os endpoints de tipos de pallet.
 *
 * <p>Endpoint documentado em {@code GET /pallet-tipos}
 * (tag {@code pallet-tipo-controller}, operationId {@code findAllActive_2}).</p>
 *
 * <p>Parâmetros suportados pela API: {@code page} (default 0), {@code size} (default 10),
 * {@code sort} (default "nome"), {@code direction} (default "asc").</p>
 *
 * <p>Utilização:</p>
 * <pre>{@code
 * IPalletTipoApiService api = RetrofitClient.getInstance()
 *         .getService(IPalletTipoApiService.class);
 * }</pre>
 */
public interface IPalletTipoApiService {

    /**
     * Devolve a lista paginada de tipos de pallet activos.
     *
     * @param page      índice da página (0-based)
     * @param size      número de itens por página
     * @param sort      campo de ordenação (default "nome")
     * @param direction direcção da ordenação ("asc" ou "desc")
     * @return {@link Call} com resposta paginada de {@link PalletTipoResponse}
     */
    @GET("pallet-tipos")
    Call<PaginatedResponse<PalletTipoResponse>> findAll(
            @Query("page")      int    page,
            @Query("size")      int    size,
            @Query("sort")      String sort,
            @Query("direction") String direction
    );
}
