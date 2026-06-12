package com.example.webapp.api;

import com.example.webapp.model.auth.LoginRequest;
import com.example.webapp.model.auth.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interface Retrofit para os endpoints de autenticação.
 *
 * <h3>Endpoint</h3>
 * <ul>
 *   <li>{@code POST /auth/login} — autentica com email e password,
 *       devolvendo um {@link LoginResponse} com o token JWT.</li>
 * </ul>
 *
 * <h3>Notas</h3>
 * <p>Este endpoint é público no backend (não requer JWT) pelo que
 * a chamada pode ser feita antes de existir uma sessão activa.</p>
 */
public interface IAuthApiService {

    /**
     * Autentica o utilizador com as credenciais fornecidas.
     *
     * @param request corpo com {@code email} e {@code password}
     * @return Call com o resultado da autenticação (id, nome, email, role, token)
     */
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
