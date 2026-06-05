package com.example.webapp.api;

import com.example.webapp.model.PaginatedResponse;
import com.example.webapp.model.catalogo.ProdutoCatalogoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Interface Retrofit para os endpoints do catálogo de produtos finais visíveis ao cliente.
 *
 * <p>Endpoint documentado em {@code GET /produtos-finais/catalogo}
 * (tag {@code produto-final-controller}, operationId {@code findAllVisivelCliente}).</p>
 *
 * <p>Parâmetros suportados pela API: {@code page} (default 0), {@code size} (default 10).
 * O endpoint <strong>não suporta</strong> {@code sort} nem {@code direction}.</p>
 *
 * <p>Utilização:</p>
 * <pre>{@code
 * IProdutoCatalogoApiService api = RetrofitClient.getInstance()
 *         .getService(IProdutoCatalogoApiService.class);
 * }</pre>
 */
public interface IProdutoCatalogoApiService {

    /**
     * Devolve a lista paginada de produtos finais visíveis ao cliente.
     *
     * @param page índice da página (0-based, default 0)
     * @param size número de itens por página (default 10)
     * @return {@link Call} com resposta paginada de {@link ProdutoCatalogoResponse}
     */
    @GET("produtos-finais/catalogo")
    Call<PaginatedResponse<ProdutoCatalogoResponse>> findAllCatalogo(
            @Query("page") int page,
            @Query("size") int size
    );
}
