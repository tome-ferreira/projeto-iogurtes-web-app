package com.example.webapp.service;

import com.example.webapp.api.IAuthApiService;
import com.example.webapp.api.RetrofitClient;
import com.example.webapp.model.auth.LoginRequest;
import com.example.webapp.model.auth.LoginResponse;
import org.springframework.stereotype.Service;
import retrofit2.Response;

import java.io.IOException;

@Service
public class AuthService {

    private final IAuthApiService api = RetrofitClient.getInstance().getService(IAuthApiService.class);

    public LoginResponse login(String email, String password) throws IOException {
        Response<LoginResponse> response = api.login(new LoginRequest(email, password)).execute();

        if (response.isSuccessful()) {
            return response.body();
        }

        return null;
    }
}
