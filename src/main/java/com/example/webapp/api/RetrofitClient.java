package com.example.webapp.api;

import com.example.webapp.config.ApiConfig;
import com.example.webapp.model.SessionUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

public final class RetrofitClient {

    Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonDeserializer<LocalDateTime>) (json, type, ctx) -> {
                        String raw = json.getAsString();
                        raw = raw.replaceAll("(\\.\\d{6})\\d+", "$1");
                        return LocalDateTime.parse(raw,
                                DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd'T'HH:mm:ss[.SSSSSS][.SSSSS][.SSSS][.SSS][.SS][.S]"));
                    })
            .registerTypeAdapter(LocalDateTime.class,
                    (JsonSerializer<LocalDateTime>) (src, type, ctx) -> new JsonPrimitive(src.toString()))
            .registerTypeAdapter(LocalDate.class,
                    (JsonDeserializer<LocalDate>) (json, type, ctx) -> LocalDate.parse(json.getAsString()))
            .registerTypeAdapter(LocalDate.class,
                    (JsonSerializer<LocalDate>) (src, type, ctx) -> new JsonPrimitive(src.toString()))
            .create();

    // Singleton
    private static volatile RetrofitClient instance;

    /**
     * Devolve a instância única (criação lazy thread-safe via double-checked
     * locking).
     */
    public static RetrofitClient getInstance() {
        if (instance == null) {
            synchronized (RetrofitClient.class) {
                if (instance == null) {
                    instance = new RetrofitClient();
                }
            }
        }
        return instance;
    }

    private final Retrofit retrofit;

    private RetrofitClient() {
        // OkHttp client
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
                .connectTimeout(ApiConfig.TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(ApiConfig.TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(ApiConfig.TIMEOUT, TimeUnit.SECONDS);

        httpClientBuilder.addInterceptor(chain -> {
            Request original = chain.request();
            try {
                ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attrs != null) {
                    HttpSession session = attrs.getRequest().getSession(false);
                    if (session != null) {
                        SessionUser user = (SessionUser) session.getAttribute("loggedUser");
                        if (user != null && user.getToken() != null) {
                            Request authenticated = original.newBuilder()
                                    .header("Authorization", "Bearer " + user.getToken())
                                    .build();
                            return chain.proceed(authenticated);
                        }
                    }
                }
            } catch (Exception ignored) {
                // Falha segura: avança sem header de autenticação
            }
            return chain.proceed(original);
        });

        if (ApiConfig.LOGGING_ENABLED) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClientBuilder.addInterceptor(loggingInterceptor);
        }

        OkHttpClient httpClient = httpClientBuilder.build();

        // ── Retrofit instance ─────────────────────────────────────────────────
        this.retrofit = new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public <T> T getService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
