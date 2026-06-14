package com.example.webapp.api;

import com.example.webapp.model.auth.LoginRequest;
import com.example.webapp.model.auth.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface IAuthApiService {

    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
