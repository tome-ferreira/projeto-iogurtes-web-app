package com.example.webapp.api;

import com.example.webapp.model.PaginatedResponse;
import com.example.webapp.model.catalogo.ProdutoCatalogoResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface Retrofit para os endpoints do catálogo de produtos finais visíveis ao cliente.
 *
 * <p>Endpoints documentados:
 * <ul>
 *   <li>{@code GET /produtos-finais/catalogo} — lista paginada (visivelCliente=true)</li>
 *   <li>{@code GET /produtos-finais/{id}} — detalhe de um produto por UUID</li>
 * </ul>
 * </p>
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

    /**
     * Devolve o detalhe completo de um produto final pelo seu UUID.
     *
     * <p>Endpoint: {@code GET /produtos-finais/{id}}
     * (operationId {@code findById_1}).</p>
     *
     * @param id UUID do produto (formato string)
     * @return {@link Call} com o {@link ProdutoCatalogoResponse} completo
     */
    @GET("produtos-finais/{id}")
    Call<ProdutoCatalogoResponse> findById(
            @Path("id") String id
    );
}
