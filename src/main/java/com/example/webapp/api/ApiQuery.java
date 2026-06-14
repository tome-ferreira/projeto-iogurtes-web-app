package com.example.webapp.api;

import retrofit2.Call;
import retrofit2.Response;

import java.util.function.Consumer;

public final class ApiQuery {

    private ApiQuery() {
    }

    public static <T> void execute(Call<T> call, Consumer<QueryState<T>> onStateChange) {
        // Notificar imediatamente que o pedido começou
        onStateChange.accept(QueryState.loading());

        // Executar o pedido HTTP de forma síncrona
        try {
            Response<T> response = call.execute();

            if (response.isSuccessful()) {
                // Sucesso
                onStateChange.accept(QueryState.success(response.body()));
            } else {
                // Tenta extrair "message" do errorBody JSON
                String message = "Erro HTTP " + response.code();
                try {
                    if (response.errorBody() != null) {
                        String errorBody = response.errorBody().string();
                        com.google.gson.JsonObject json = com.google.gson.JsonParser
                                .parseString(errorBody).getAsJsonObject();
                        if (json.has("message")) {
                            message = json.get("message").getAsString();
                        }
                    }
                } catch (Exception ignored) {
                }

                onStateChange.accept(QueryState.error(message, null));
            }

        } catch (Exception t) {
            // Falha de rede / IO
            String message = t.getMessage() != null
                    ? t.getMessage()
                    : "Falha de rede desconhecida";
            onStateChange.accept(QueryState.error(message, t));
        }
    }
}
