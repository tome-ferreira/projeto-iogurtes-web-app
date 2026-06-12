package com.example.webapp.service;

import com.example.webapp.api.IAuthApiService;
import com.example.webapp.api.RetrofitClient;
import com.example.webapp.model.auth.LoginRequest;
import com.example.webapp.model.auth.LoginResponse;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;

/**
 * Serviço de autenticação — faz a chamada síncrona ao endpoint POST /auth/login.
 *
 * <h3>Uso</h3>
 * <pre>{@code
 * LoginResponse resp = authService.login(email, password);
 * // resp == null se as credenciais forem inválidas (HTTP 4xx)
 * // lança IOException se houver falha de rede
 * }</pre>
 *
 * <h3>Nota</h3>
 * <p>Utiliza {@code .execute()} (chamada síncrona), adequado para
 * Spring MVC server-side onde cada pedido corre na sua própria thread.</p>
 */
@Service
public class AuthService {

    private final IAuthApiService api =
            RetrofitClient.getInstance().getService(IAuthApiService.class);

    /**
     * Autentica o utilizador com as credenciais fornecidas.
     *
     * @param email    endereço de e-mail do utilizador
     * @param password palavra-passe em texto simples
     * @return {@link LoginResponse} com os dados do utilizador e o token JWT,
     *         ou {@code null} se as credenciais forem rejeitadas (HTTP 4xx)
     * @throws IOException se ocorrer um erro de rede ou de I/O
     */
    public LoginResponse login(String email, String password) throws IOException {
        Response<LoginResponse> response =
                api.login(new LoginRequest(email, password)).execute();

        if (response.isSuccessful()) {
            return response.body();
        }
        // 401/403 — credenciais inválidas; o controller trata
        return null;
    }
}
