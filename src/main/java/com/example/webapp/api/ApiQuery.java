package com.example.webapp.api;

import retrofit2.Call;
import retrofit2.Response;

import java.util.function.Consumer;

/**
 * Utilitário de execução de pedidos Retrofit integrado com o padrão
 * ReactQuery-inspired adoptado neste projecto.
 *
 * <h3>Diferença face à Desktop App</h3>
 * <p>
 * Na aplicação desktop (JavaFX) o {@code ApiQuery} usa {@code Platform.runLater()}
 * para devolver o resultado na JavaFX Application Thread.<br>
 * Na web app (Spring Boot / Servlet) não existe JavaFX. O método
 * {@link #execute(Call, Consumer)} executa o pedido de forma <strong>síncrona</strong>
 * via {@link Call#execute()} e notifica o caller directamente na thread chamante
 * (tipicamente a thread do servlet ou uma thread marcada com {@code @Async}).
 * </p>
 *
 * <h3>Responsabilidades</h3>
 * <ol>
 * <li>Emite {@link QueryState#loading()} <em>imediatamente</em> antes de executar o pedido HTTP.</li>
 * <li>Executa o pedido HTTP de forma síncrona via {@link Call#execute()}.</li>
 * <li>Emite {@link QueryState#success(Object)} ou {@link QueryState#error(String, Throwable)}
 *     directamente na thread chamante.</li>
 * </ol>
 *
 * <h3>Exemplo de utilização num Service Spring</h3>
 *
 * <pre>{@code
 * // 1. Obter a interface Retrofit
 * IEmpresaApiService api = RetrofitClient.getInstance().getService(IEmpresaApiService.class);
 *
 * // 2. Criar o Call
 * Call<List<EmpresaResponse>> call = api.findAll();
 *
 * // 3. Executar via ApiQuery
 * ApiQuery.execute(call, state -> {
 *     if (state.isLoading())  { /* pedido iniciado *\/ }
 *     if (state.isSuccess())  { /* usar state.getData() *\/ }
 *     if (state.isError())    { /* mostrar state.getErrorMessage() *\/ }
 * });
 * }</pre>
 */
public final class ApiQuery {

    /** Classe utilitária — não instanciável. */
    private ApiQuery() {
    }

    /**
     * Executa um pedido Retrofit de forma síncrona e notifica o chamador
     * através do {@code onStateChange} com transições de estado.
     *
     * <p>
     * Sequência de chamadas garantidas:
     * </p>
     * <ol>
     * <li>{@code onStateChange(QueryState.loading())} — imediatamente, na thread do chamador.</li>
     * <li>{@code onStateChange(QueryState.success(body))} <em>ou</em>
     *     {@code onStateChange(QueryState.error(...))} — após a resposta HTTP,
     *     na mesma thread do chamador.</li>
     * </ol>
     *
     * @param <T>           tipo do corpo da resposta esperada pelo Retrofit
     * @param call          pedido Retrofit ainda não executado
     * @param onStateChange callback invocado em cada transição de estado
     */
    public static <T> void execute(Call<T> call, Consumer<QueryState<T>> onStateChange) {
        // ── 1. Notificar imediatamente que o pedido começou ──────────────────
        onStateChange.accept(QueryState.loading());

        // ── 2. Executar o pedido HTTP de forma síncrona ──────────────────────
        try {
            Response<T> response = call.execute();

            if (response.isSuccessful()) {
                // ── 3a. Sucesso ───────────────────────────────────────────────
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
            // ── 3b. Falha de rede / IO ────────────────────────────────────────
            String message = t.getMessage() != null
                    ? t.getMessage()
                    : "Falha de rede desconhecida";
            onStateChange.accept(QueryState.error(message, t));
        }
    }
}
