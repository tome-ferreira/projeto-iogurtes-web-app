# ANALISE_WEB.md — Análise Técnica Exaustiva da Aplicação Web

> **Nota de utilização:** Este documento foi gerado pela leitura directa de todos os ficheiros fonte
> do projecto `web-app`. Todos os números de linha referem-se ao ficheiro exacto indicado no caminho.
> O estudante deve usar este documento como referência primária para redigir a secção *Frontend*
> do relatório académico.

---

## 1. Stack Tecnológica

### 1.1 Identificação completa de tecnologias e versões

A aplicação web é uma aplicação Spring Boot que funciona no modelo **server-side rendering** (SSR),
sem nenhuma *framework* JavaScript do lado do cliente. Toda a lógica de interface é gerada no servidor
e enviada ao browser como HTML completo e estático.

| Camada | Tecnologia | Versão | Papel Exacto |
|---|---|---|---|
| Framework base | Spring Boot | **4.0.6** | Auto-configuração, servidor embutido (Tomcat), gestão de dependências |
| Framework MVC | Spring Web MVC | (incluído no Spring Boot 4.0.6) | Tratamento de rotas HTTP, injecção de dependências, Model |
| Motor de templates | Thymeleaf | (incluído em `spring-boot-starter-thymeleaf`) | Renderização server-side de HTML; substitui JSP |
| Cliente HTTP | Retrofit 2 | **2.11.0** | Tipificação de chamadas HTTP à API REST do backend |
| Conversor JSON | Gson (via `converter-gson`) | **2.11.0** | Deserialização de respostas JSON da API para objectos Java |
| HTTP layer | OkHttp 3/4 | **4.12.0** | Transporte HTTP subjacente ao Retrofit; gestão de timeouts e interceptors |
| Logging HTTP | OkHttp logging-interceptor | **4.12.0** | Registo dos corpos de pedidos e respostas HTTP na consola |
| CSS framework | Tailwind CSS | CDN (sem versão fixa no pom.xml) | Classes utilitárias; carregado via CDN |
| Componentes UI | daisyUI | **4.12.10** (via CDN jsdelivr) | Componentes UI prontos (btn, badge, card, stats, modal, etc.) sobre Tailwind |
| Ícones | Font Awesome | **6.5.1** (via CDN cdnjs) | Ícones vectoriais usados em toda a interface |
| Tipografia | Plus Jakarta Sans | Google Fonts | Fonte principal de toda a interface autenticada e landing page |
| Build tool | Maven | (mvnw incluído) | Gestão de dependências e compilação |
| Java | Java | **21** | Versão do runtime, conforme `<java.version>21</java.version>` no pom.xml |

**Fonte:** `pom.xml` (linhas 8, 30, 46, 51, 57, 62); `fragments/layout.html` (linhas 8–15); `index.html` (linhas 8–15).

### 1.2 Declaração completa de dependências Maven

Ficheiro: `pom.xml` (linhas 32–75).

```xml
<!-- Spring Boot Starter — Thymeleaf (linhas 33–36) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>

<!-- Spring Boot Starter — Web MVC (linhas 37–40) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webmvc</artifactId>
</dependency>

<!-- Retrofit 2 (linhas 43–47) -->
<dependency>
    <groupId>com.squareup.retrofit2</groupId>
    <artifactId>retrofit</artifactId>
    <version>2.11.0</version>
</dependency>

<!-- Converter Gson para Retrofit (linhas 48–52) -->
<dependency>
    <groupId>com.squareup.retrofit2</groupId>
    <artifactId>converter-gson</artifactId>
    <version>2.11.0</version>
</dependency>

<!-- OkHttp (linhas 54–58) -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>okhttp</artifactId>
    <version>4.12.0</version>
</dependency>

<!-- OkHttp Logging Interceptor (linhas 59–63) -->
<dependency>
    <groupId>com.squareup.okhttp3</groupId>
    <artifactId>logging-interceptor</artifactId>
    <version>4.12.0</version>
</dependency>
```

### 1.3 Ausência de JavaScript framework do lado do cliente

A aplicação não usa React, Angular, Vue, Svelte, ou qualquer outra *framework* JavaScript
do lado do cliente. O único JavaScript presente é **vanilla JS mínimo** em `produto-detalhe.html`
(linhas 196–203) para sincronizar um atributo `data-nome` de um `<select>` para um `<input hidden>`.
Não há estado no cliente, não há chamadas AJAX, não há routing no browser.

---

## 2. Arquitetura do Projecto

### 2.1 Estrutura completa de pastas e explicação de cada package

```
web-app/
├── pom.xml                                          ← Dependências Maven
├── HELP.md / README.md                              ← Documentação sumária
├── docs/
│   ├── WEB-APP-REFERENCE.md                         ← Guia de referência do projecto
│   ├── AUTENTICACAO.md                              ← Documentação de autenticação
│   └── SessionService.md                            ← Documentação da sessão
├── src/main/
│   ├── java/com/example/webapp/
│   │   ├── WebappApplication.java                   ← Entry point @SpringBootApplication
│   │   ├── api/                                     ← Camada de acesso HTTP à API REST
│   │   │   ├── RetrofitClient.java                  ← Singleton que configura OkHttp + Retrofit
│   │   │   ├── ApiConfig.java                       ← Lê config.properties (BASE_URL, TIMEOUT…)
│   │   │   ├── ApiQuery.java                        ← Executor síncrono ReactQuery-inspired
│   │   │   ├── QueryState.java                      ← State machine: IDLE→LOADING→SUCCESS|ERROR
│   │   │   ├── IAuthApiService.java                 ← Interface Retrofit: POST /auth/login
│   │   │   ├── IEncomendaApiService.java            ← Interface Retrofit: CRUD encomendas
│   │   │   ├── IMoedaApiService.java                ← Interface Retrofit: GET /moedas/codigo/{c}
│   │   │   ├── IPalletTipoApiService.java           ← Interface Retrofit: GET /pallet-tipos
│   │   │   ├── IProdutoCatalogoApiService.java      ← Interface Retrofit: GET /produtos-finais
│   │   │   └── services/                            ← (directório vazio, só tem .gitkeep)
│   │   ├── config/                                  ← Configuração Spring
│   │   │   ├── ApiConfig.java                       ← Carrega config.properties (estático)
│   │   │   ├── AuthInterceptor.java                 ← HandlerInterceptor: guarda de rotas
│   │   │   └── WebMvcConfig.java                    ← Regista AuthInterceptor no MVC
│   │   ├── controller/                              ← Camada de controlo HTTP
│   │   │   ├── WebController.java                   ← GET /
│   │   │   ├── AuthController.java                  ← GET+POST /login, POST /logout
│   │   │   ├── CatalogoController.java              ← GET /catalogo
│   │   │   ├── ProdutoDetalheController.java        ← GET /produto/{id} + POST /adicionar-carrinho
│   │   │   ├── CarrinhoController.java              ← GET /carrinho, POST /finalizar, /remover
│   │   │   ├── DashboardController.java             ← GET /client-area
│   │   │   ├── MinhasEncomendasController.java      ← GET /encomendas
│   │   │   ├── EncomendaDetalheController.java      ← GET /encomendas/{id}, POST /cancelar
│   │   │   └── EncomendaCanceladaController.java    ← GET /encomenda-cancelada-sucesso|erro
│   │   ├── model/                                   ← POJOs de dados
│   │   │   ├── PaginatedResponse.java               ← Wrapper genérico de paginação da API
│   │   │   ├── SessionUser.java                     ← Utilizador autenticado em sessão
│   │   │   ├── auth/
│   │   │   │   ├── LoginRequest.java                ← Corpo POST /auth/login
│   │   │   │   └── LoginResponse.java               ← Resposta de /auth/login
│   │   │   ├── carrinho/
│   │   │   │   ├── Carrinho.java                    ← Lista de itens; Serializable para sessão
│   │   │   │   └── CarrinhoItem.java                ← Uma linha do carrinho
│   │   │   ├── catalogo/
│   │   │   │   ├── ProdutoCatalogoResponse.java     ← Produto do catálogo
│   │   │   │   └── PalletTipoResponse.java          ← Tipo de pallet
│   │   │   ├── encomenda/
│   │   │   │   ├── CreateEncomendaRequest.java      ← Payload POST /encomendas
│   │   │   │   ├── EncomendaPalletItem.java         ← Um pallet dentro de CreateEncomendaRequest
│   │   │   │   ├── EncomendaResumoResponse.java     ← Encomenda resumida (lista)
│   │   │   │   ├── EncomendaDetalheResponse.java    ← Encomenda completa (detalhe)
│   │   │   │   └── EncomendaPalletResponse.java     ← Pallet dentro de uma encomenda
│   │   │   └── moeda/
│   │   │       └── MoedaResponse.java               ← Moeda (EUR, etc.)
│   │   ├── service/                                 ← Camada de serviços de aplicação
│   │   │   ├── AuthService.java                     ← Chama POST /auth/login de forma síncrona
│   │   │   ├── SessionService.java                  ← Gere SessionUser na sessão HTTP
│   │   │   ├── CarrinhoService.java                 ← Gere Carrinho na sessão HTTP
│   │   │   ├── EncomendaService.java                ← CRUD de encomendas via API
│   │   │   ├── MoedaService.java                    ← Obter moeda por código
│   │   │   ├── PalletTipoService.java               ← Lista tipos de pallet via API
│   │   │   └── ProdutoCatalogoService.java          ← Lista e detalhe de produtos via API
│   │   └── util/
│   │       └── EnumDisplayHelper.java               ← @Component: converte enums em português
│   └── resources/
│       ├── application.properties                   ← spring.application.name=webapp
│       ├── config.properties                        ← api.base.url, api.timeout, api.logging
│       ├── static/
│       │   ├── css/
│       │   │   └── dashboard.css                    ← CSS customizado mínimo (scrollbar)
│       │   └── images/                              ← Imagens estáticas
│       └── templates/
│           ├── fragments/
│           │   └── layout.html                      ← Layout partilhado (sidebar, navbar, head)
│           ├── index.html                           ← Landing page pública
│           ├── login.html                           ← Formulário de login (independente)
│           ├── client-area.html                     ← Dashboard B2B
│           ├── catalogo.html                        ← Catálogo paginado de produtos
│           ├── produto-detalhe.html                 ← Detalhe de produto + carrinho
│           ├── carrinho.html                        ← Carrinho de compras
│           ├── checkout.html                        ← Sucesso de checkout
│           ├── checkout-error.html                  ← Erro de checkout
│           ├── encomendas.html                      ← Histórico de encomendas
│           ├── encomenda-detalhe.html               ← Detalhe de encomenda
│           ├── encomenda-cancelada-sucesso.html     ← Feedback de cancelamento com sucesso
│           └── encomenda-cancelada-erro.html        ← Feedback de cancelamento com erro
```

### 2.2 Modelo de Server-Side Rendering (SSR)

O ciclo de vida de um pedido HTTP nesta aplicação é completamente diferente de uma
*Single-Page Application* (SPA) como as que usam React ou Angular. O diagrama abaixo descreve
o fluxo real:

```
Browser                Spring MVC           Service Layer         API REST Backend
   │                      │                      │                      │
   │── GET /catalogo ────>│                      │                      │
   │                      │── getCatalogo(0,10) >│                      │
   │                      │                      │── GET /produtos-finais/catalogo?page=0&size=10 ─>│
   │                      │                      │<────────────── 200 OK {content:[...], ...} ──────│
   │                      │<── PaginatedResponse ─│                      │
   │                      │                      │                      │
   │                      │── Model.addAttribute("produtos", ...) ─>    │
   │                      │── return "catalogo" ─>                      │
   │                      │ (Thymeleaf renderiza catalogo.html + dados)  │
   │<── 200 OK HTML completo ─────────────────────────────────────────  │
   │  (com tabela já preenchida, paginação já calculada, etc.)          │
```

**Contraste com SPA:**

| Aspecto | Esta Web App (SSR) | SPA (React, Angular, etc.) |
|---|---|---|
| Renderização | No servidor, antes de enviar | No browser, após receber dados |
| Estado | Sem estado no browser; toda a lógica no servidor | Estado mantido no browser (useState, Zustand, etc.) |
| Chamadas API | O servidor faz as chamadas HTTP (Retrofit) | O browser faz as chamadas (fetch, axios) |
| Navegação | Cada URL faz um pedido HTTP completo | Routing do lado do cliente (React Router) |
| Loading states | Não necessários: o browser aguarda o HTML completo | Necessários: spinners, skeletons, etc. |
| JavaScript | Mínimo (só para UI local) | Grande bundle JS necessário |

### 2.3 Ponto de entrada da aplicação

Ficheiro: `src/main/java/com/example/webapp/WebappApplication.java` (linhas 1–13).

```java
@SpringBootApplication
public class WebappApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebappApplication.class, args);
    }
}
```

A anotação `@SpringBootApplication` activa o component scanning automático de todos os beans
(`@Controller`, `@Service`, `@Component`, `@Configuration`) no package `com.example.webapp`
e sub-packages.

---

## 3. Cliente HTTP — Inspiração em React Query e Adaptação para Web

### 3.1 RetrofitClient (versão web)

**Ficheiro:** `src/main/java/com/example/webapp/api/RetrofitClient.java` (169 linhas)

O `RetrofitClient` é um **Singleton** que encapsula toda a configuração do cliente HTTP.
Na versão web, difere da versão desktop em aspectos fundamentais.

#### 3.1.1 Padrão Singleton com double-checked locking

```java
// RetrofitClient.java — linhas 80–94
private static volatile RetrofitClient instance;

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
```

O campo `volatile` garante visibilidade entre threads. O *double-checked locking* garante
que a instância é criada apenas uma vez, mesmo com múltiplos pedidos concorrentes (o servidor
web serve múltiplos utilizadores em simultâneo).

**Nota importante sobre a diferença face à versão desktop:** A versão desktop poderia usar
um campo `static` simples inicializado no arranque porque a aplicação JavaFX tem uma única
*Application Thread*. Na versão web, múltiplas *servlet threads* acedem ao Singleton em
simultâneo — daí o `volatile` e o `synchronized`.

#### 3.1.2 Configuração Gson com adaptadores de tipos temporais

```java
// RetrofitClient.java — linhas 59–74
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
```

O adaptador de `LocalDateTime` é particularmente importante: a API REST pode devolver
timestamps com precisão variável (`.SSSSSS` a `.S`). A expressão regular `(\\.\\d{6})\\d+`
normaliza para no máximo 6 casas decimais antes do parse. Sem isto, o Gson lançaria excepções
ao deserializar timestamps da API.

#### 3.1.3 Interceptor de autenticação JWT — mecanismo de RequestContextHolder

```java
// RetrofitClient.java — linhas 114–135
httpClientBuilder.addInterceptor(chain -> {
    Request original = chain.request();
    try {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
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
```

**Este é o ponto técnico mais crítico do RetrofitClient e merece análise detalhada:**

O problema fundamental é: **como é que um Singleton partilhado por todas as threads
obtém o token JWT do utilizador correcto?**

Em Spring MVC, cada pedido HTTP é servido por uma *thread* de servlet separada. Se o token
JWT fosse guardado num campo estático do RetrofitClient (ex.: `static String currentToken`),
o token do utilizador A seria sobrescrito pelo token do utilizador B quando dois pedidos
chegassem ao mesmo tempo — uma falha de segurança grave.

A solução é `RequestContextHolder.getRequestAttributes()`, uma API do Spring que guarda
o contexto do pedido HTTP actual numa `ThreadLocal`. Dado que cada pedido tem a sua própria
thread, o acesso ao contexto é automaticamente isolado por utilizador. O fluxo é:

1. O browser envia um pedido HTTP com o cookie de sessão.
2. Spring MVC recebe o pedido na sua thread de servlet e associa o `HttpServletRequest`
   ao `RequestContextHolder` (numa `ThreadLocal` da thread corrente).
3. O interceptor OkHttp (que corre na mesma thread) lê `RequestContextHolder.getRequestAttributes()`
   — obtém o `HttpServletRequest` da thread corrente.
4. Extrai a `HttpSession` e o `SessionUser` (que contém o token JWT).
5. Injenta o cabeçalho `Authorization: Bearer <token>` no pedido Retrofit.
6. A API REST recebe o pedido com o token correcto para o utilizador correcto.

Se o pedido Retrofit corresse numa thread de fundo sem contexto de servlet activo,
`getRequestAttributes()` devolveria `null`, o interceptor avançaria sem `Authorization`
e a API rejeitaria o pedido (se o endpoint for protegido). O bloco `catch (Exception ignored)`
garante que mesmo nesse caso a aplicação não lança excepções (falha segura).

#### 3.1.4 Configuração OkHttp e Retrofit

```java
// RetrofitClient.java — linhas 104–150
OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder()
        .connectTimeout(ApiConfig.TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(ApiConfig.TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(ApiConfig.TIMEOUT, TimeUnit.SECONDS);
// ... (interceptor JWT e logging)
OkHttpClient httpClient = httpClientBuilder.build();

this.retrofit = new Retrofit.Builder()
        .baseUrl(ApiConfig.BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build();
```

Os timeouts são lidos de `config.properties` via `ApiConfig`: `api.timeout.seconds=30`.

#### 3.1.5 Método getService — criação de proxies Retrofit

```java
// RetrofitClient.java — linhas 165–167
public <T> T getService(Class<T> serviceClass) {
    return retrofit.create(serviceClass);
}
```

Cada serviço usa este método para obter um proxy dinâmico da sua interface Retrofit:

```java
// Exemplo em ProdutoCatalogoService.java — linhas 35–36
private final IProdutoCatalogoApiService api =
        RetrofitClient.getInstance().getService(IProdutoCatalogoApiService.class);
```

#### 3.1.6 Logging HTTP

```java
// RetrofitClient.java — linhas 137–141
if (ApiConfig.LOGGING_ENABLED) {
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    httpClientBuilder.addInterceptor(loggingInterceptor);
}
```

Controlado por `api.logging.enabled=true` em `config.properties`. Com `Level.BODY`,
o OkHttp regista na consola os cabeçalhos e corpos completos de cada pedido e resposta HTTP.
Útil em desenvolvimento, deve estar `false` em produção.

### 3.2 Configuração de propriedades — ApiConfig

**Ficheiro:** `src/main/java/com/example/webapp/config/ApiConfig.java` (61 linhas)

```java
// ApiConfig.java — linhas 21–60
public final class ApiConfig {

    public static final String BASE_URL;    // ex.: "http://localhost:8081/"
    public static final int TIMEOUT;        // ex.: 30 (segundos)
    public static final boolean LOGGING_ENABLED; // ex.: true

    static {
        Properties props = new Properties();
        try (InputStream is = ApiConfig.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            // ...
            props.load(is);
        }
        BASE_URL        = props.getProperty("api.base.url", "http://localhost:8081/");
        TIMEOUT         = Integer.parseInt(props.getProperty("api.timeout.seconds", "30"));
        LOGGING_ENABLED = Boolean.parseBoolean(props.getProperty("api.logging.enabled", "false"));
    }
}
```

O bloco `static` é executado exactamente uma vez, na primeira referência à classe.
Os valores são imutáveis (`final`) e acessíveis como constantes estáticas públicas.

**Ficheiro de configuração:** `src/main/resources/config.properties` (11 linhas):

```properties
api.base.url=http://localhost:8081/
api.timeout.seconds=30
api.logging.enabled=true
```

### 3.3 Ausência de QueryState/ApiQuery na forma da aplicação desktop

A aplicação web **tem** as classes `QueryState` e `ApiQuery` (herdadas/adaptadas da versão
desktop), mas o seu papel é diferente.

**Ficheiro:** `src/main/java/com/example/webapp/api/QueryState.java` (170 linhas)

O `QueryState` encapsula quatro estados: `IDLE`, `LOADING`, `SUCCESS`, `ERROR`.

```java
// QueryState.java — linhas 39–44
public enum Status {
    IDLE,
    LOADING,
    SUCCESS,
    ERROR
}
```

**Ficheiro:** `src/main/java/com/example/webapp/api/ApiQuery.java` (109 linhas)

O `ApiQuery.execute()` executa o pedido **sincronamente**:

```java
// ApiQuery.java — linhas 71–107
public static <T> void execute(Call<T> call, Consumer<QueryState<T>> onStateChange) {
    onStateChange.accept(QueryState.loading());  // LOADING imediato

    try {
        Response<T> response = call.execute();  // ← SÍNCRONO

        if (response.isSuccessful()) {
            onStateChange.accept(QueryState.success(response.body()));
        } else {
            // extrai "message" do errorBody JSON
            onStateChange.accept(QueryState.error(message, null));
        }
    } catch (Exception t) {
        onStateChange.accept(QueryState.error(message, t));
    }
}
```

**Porque é que não são necessários estados de loading na versão web?**

Na versão desktop (JavaFX), os pedidos HTTP correm em *background threads* (para não bloquear
a UI thread). O `QueryState.LOADING` é essencial para mostrar um spinner enquanto o pedido está
em curso na thread de fundo, e o `Platform.runLater()` actualiza a UI quando o resultado chega.

Na versão web, a arquitectura é diferente: o browser envia um pedido GET para o servidor,
a thread de servlet **fica bloqueada** enquanto chama `.execute()` (pedido síncrono ao backend),
o Thymeleaf renderiza o HTML com os dados já prontos, e o browser recebe o HTML completo.
O utilizador vê um indicador de carregamento nativo do browser (barra de progressão do tab).
Não há UI dinâmica que precise de ser actualizada durante o pedido.

**Comparação directa:**

| Aspecto | Desktop (JavaFX) | Web (Spring MVC) |
|---|---|---|
| Execução | `.enqueue()` — assíncrona em background thread | `.execute()` — síncrona na servlet thread |
| Update UI | `Platform.runLater()` necessário | Não necessário; o HTML é gerado depois do pedido |
| Loading state | `LOADING` mostrado enquanto espera | Browser mostra indicador nativo |
| IDLE/LOADING/SUCCESS/ERROR | Usados para controlar UI reativa | Só SUCCESS e ERROR têm relevância prática |

### 3.4 Exemplo concreto de método síncrono de serviço

**ProdutoCatalogoService.getCatalogo(int page, int size)**

**Ficheiro:** `src/main/java/com/example/webapp/service/ProdutoCatalogoService.java` (linhas 46–66)

```java
public PaginatedResponse<ProdutoCatalogoResponse> getCatalogo(int page, int size) {
    @SuppressWarnings("unchecked")
    final PaginatedResponse<ProdutoCatalogoResponse>[] result = new PaginatedResponse[1];
    final String[] errorMessage = {null};

    ApiQuery.execute(api.findAllCatalogo(page, size), state -> {
        if (state.isSuccess()) {
            result[0] = state.getData();
        } else if (state.isError()) {
            errorMessage[0] = state.getErrorMessage();
            log.error("Erro ao obter catálogo de produtos: {}", state.getErrorMessage(),
                    state.getError());
        }
    });

    if (errorMessage[0] != null) {
        throw new ProdutoCatalogoException(errorMessage[0]);
    }

    return result[0];
}
```

**Pormenor técnico:** Como o lambda em Java não pode capturar variáveis modificáveis locais,
usa-se o padrão de arrays de um elemento (`final PaginatedResponse[] result = new PaginatedResponse[1]`).
O array em si é `final` (imutável como referência), mas `result[0]` pode ser modificado dentro
do lambda.

### 3.5 PaginatedResponse\<T\> — Replicação do padrão desktop

**Ficheiro:** `src/main/java/com/example/webapp/model/PaginatedResponse.java` (58 linhas)

```java
// PaginatedResponse.java — linhas 24–57
public class PaginatedResponse<T> {

    /** Lista de itens na página corrente. */
    public List<T> content;           // linha 27

    /** Total de registos em todas as páginas. */
    public long totalElements;        // linha 30

    /** Total de páginas. */
    public int totalPages;            // linha 33

    /**
     * Índice da página corrente (0-based).
     * Corresponde ao campo "number" do Spring Page JSON.
     */
    public int number;                // linha 39

    /** Dimensão da página (registos por página). */
    public int size;                  // linha 42

    /** true se esta for a primeira página. */
    public boolean first;             // linha 45

    /** true se esta for a última página. */
    public boolean last;              // linha 48

    /** Quantidade de itens nesta página. */
    public int numberOfElements;      // linha 51

    /** true se content estiver vazio. */
    public boolean empty;             // linha 54

    public PaginatedResponse() {}     // linha 56
}
```

Os campos são `public` (sem getters/setters) porque o Gson usa reflexão directa para
deserializar — não são necessários métodos de acesso para o desserializador.

Os nomes dos campos **correspondem exactamente** às chaves JSON devolvidas pela API Spring
(baseada em `org.springframework.data.domain.Page`). Este é um detalhe crítico: qualquer
discrepância de nomes faz com que o Gson deixe os campos a `null`/`0`/`false`.

#### 3.5.1 Utilização em CatalogoController

**Ficheiro:** `src/main/java/com/example/webapp/controller/CatalogoController.java` (linhas 52–76)

```java
@GetMapping("/catalogo")
public String catalogo(
        @RequestParam(defaultValue = "0")  int page,   // linha 54
        @RequestParam(defaultValue = "10") int size,   // linha 55
        Model model                                     // linha 56
) {
    PaginatedResponse<ProdutoCatalogoResponse> produtos;
    try {
        produtos = produtoCatalogoService.getCatalogo(page, size);   // linha 61
    } catch (ProdutoCatalogoService.ProdutoCatalogoException e) {
        log.warn("Falha ao carregar catálogo de produtos: {}", e.getMessage());
        produtos = ProdutoCatalogoService.emptyResponse();
        model.addAttribute("errorMessage",
                "Não foi possível carregar o catálogo neste momento. Por favor, tente mais tarde.");
    }

    model.addAttribute("produtos",        produtos);     // linha 70
    model.addAttribute("currentPage",     page);         // linha 71
    model.addAttribute("pageSize",        size);         // linha 72
    model.addAttribute("pageSizeOptions", PAGE_SIZE_OPTIONS); // linha 73

    return "catalogo";                                   // linha 75
}
```

`PAGE_SIZE_OPTIONS` é definido na linha 39: `List.of(5, 10, 20, 50)`.

#### 3.5.2 Utilização em catalogo.html — th:each

**Ficheiro:** `src/main/resources/templates/catalogo.html`

A iteração de produtos com `th:each` (linha 79):

```html
<!-- catalogo.html — linha 79 -->
<tr th:each="produto : ${produtos.content}"
    class="hover:bg-slate-50/60 transition-colors border-b border-slate-100 last:border-0">

    <!-- Nome + SKU — linhas 83–88 -->
    <td>
        <div class="flex flex-col">
            <span class="font-semibold text-slate-800" th:text="${produto.nome}">—</span>
            <span class="text-xs text-slate-400 font-mono mt-0.5" th:text="${produto.codigoSku}">—</span>
        </div>
    </td>

    <!-- Estado Físico com EnumDisplayHelper — linhas 92–96 -->
    <td>
        <span th:with="label=${@enumDisplayHelper.getEstadoFisicoLabel(produto.estadoFisico)}"
              th:text="${label}"
              th:classappend="${produto.estadoFisico == 'LIQUIDO'} ? 'badge badge-info badge-outline' : 'badge badge-warning badge-outline'"
              class="badge font-medium">—</span>
    </td>

    <!-- Preço por kg — linhas 99–102 -->
    <td class="text-right">
        <span class="font-bold text-slate-800"
              th:text="${produto.precoPorKg != null ? #numbers.formatDecimal(produto.precoPorKg, 1, 2) + ' €' : '—'}">—</span>
    </td>

    <!-- Link para detalhe — linhas 105–110 -->
    <td class="text-center">
        <a th:href="@{/produto/{id}(id=${produto.id})}"
           class="btn btn-sm bg-blue-50 text-blue-700 ...">
            <i class="fa-solid fa-eye mr-1"></i> Detalhes
        </a>
    </td>
</tr>
```

#### 3.5.3 Controlos de paginação em catalogo.html

**Ficheiro:** `src/main/resources/templates/catalogo.html` (linhas 118–156)

```html
<!-- catalogo.html — linhas 118–156: bloco de paginação completo -->
<div th:if="${produtos.totalPages > 0}"
     class="flex flex-col sm:flex-row items-center justify-between gap-4 pt-2">

    <!-- Indicador de página — linhas 123–130 -->
    <p class="text-sm text-slate-500 font-medium">
        Página <span class="font-bold text-slate-700"
                     th:text="${currentPage + 1}">1</span>
        de <span class="font-bold text-slate-700"
                  th:text="${produtos.totalPages}">1</span>
        &nbsp;·&nbsp;
        <span th:text="${produtos.totalElements}">0</span> produto(s) no total
    </p>

    <!-- Botões de navegação — linhas 133–155 -->
    <div class="join shadow-sm">

        <!-- Botão Anterior (activo) — linhas 135–139 -->
        <a th:if="${!produtos.first}"
           th:href="@{/catalogo(page=${currentPage - 1}, size=${pageSize})}"
           class="join-item btn btn-sm bg-white border border-slate-300 ...">
            <i class="fa-solid fa-chevron-left mr-1"></i> Anterior
        </a>

        <!-- Botão Anterior (desactivado) — linhas 140–143 -->
        <span th:if="${produtos.first}"
              class="join-item btn btn-sm bg-slate-50 border border-slate-200 text-slate-400 font-semibold pointer-events-none">
            <i class="fa-solid fa-chevron-left mr-1"></i> Anterior
        </span>

        <!-- Botão Próxima (activo) — linhas 146–150 -->
        <a th:if="${!produtos.last}"
           th:href="@{/catalogo(page=${currentPage + 1}, size=${pageSize})}"
           class="join-item btn btn-sm bg-white border border-slate-300 ...">
            Próxima <i class="fa-solid fa-chevron-right ml-1"></i>
        </a>

        <!-- Botão Próxima (desactivado) — linhas 151–154 -->
        <span th:if="${produtos.last}"
              class="join-item btn btn-sm bg-slate-50 border border-slate-200 text-slate-400 font-semibold pointer-events-none">
            Próxima <i class="fa-solid fa-chevron-right ml-1"></i>
        </span>
    </div>
</div>
```

A lógica é directa: quando `produtos.first == true` (primeira página), o botão "Anterior"
é substituído por um `<span>` não clicável (`pointer-events-none`). Quando `produtos.last == true`
(última página), o mesmo acontece para "Próxima". Os `<a>` só existem quando a navegação é possível.

**Selector de tamanho de página** (catalogo.html, linhas 15–27):

```html
<form method="get" action="/catalogo" class="flex items-center gap-2">
    <label for="page-size-select" class="text-sm font-medium text-slate-500 whitespace-nowrap">
        Por página:
    </label>
    <select id="page-size-select" name="size"
            onchange="this.form.submit()"
            class="select select-bordered select-sm ...">
        <option th:each="opt : ${pageSizeOptions}"
                th:value="${opt}"
                th:text="${opt}"
                th:selected="${opt == pageSize}">10</option>
    </select>
    <!-- Manter page=0 ao mudar o tamanho -->
    <input type="hidden" name="page" value="0">
</form>
```

O `onchange="this.form.submit()"` submete o formulário imediatamente ao mudar o tamanho.
O campo `<input type="hidden" name="page" value="0">` garante que ao mudar o tamanho de página
se regressa à primeira página, evitando que se fique numa página inexistente.

---

## 4. Autenticação e Sessão Web

### 4.1 SessionUser.java e SessionService.java

#### 4.1.1 SessionUser — Campos e Construtor

**Ficheiro:** `src/main/java/com/example/webapp/model/SessionUser.java` (28 linhas)

```java
// SessionUser.java — linhas 5–27
public class SessionUser {
    private UUID id;       // linha 6 — UUID do utilizador na API
    private String nome;   // linha 7 — nome de exibição
    private String email;  // linha 8 — e-mail de login
    private String role;   // linha 9 — papel: "CLIENTE", "ADMIN", etc.
    private String token;  // linha 10 — token JWT (definido após login)

    public SessionUser(UUID id, String nome, String email, String role) { // linha 12
        this.id    = id;
        this.nome  = nome;
        this.email = email;
        this.role  = role;
    }

    // Getters — linhas 20–24
    public UUID getId()      { return id; }
    public String getNome()  { return nome; }
    public String getEmail() { return email; }
    public String getRole()  { return role; }
    public String getToken() { return token; }

    // Setter do token — linha 27
    public void setToken(String token) { this.token = token; }
}
```

O token é definido separadamente após a construção do objecto porque o construtor não o recebe.
Isso é intencional: o objecto é criado a partir dos campos `LoginResponse`, e o token é adicionado
de imediato a seguir.

#### 4.1.2 SessionService — Gestão da Sessão HTTP

**Ficheiro:** `src/main/java/com/example/webapp/service/SessionService.java` (66 linhas)

```java
// SessionService.java — linha 25
private static final String SESSION_KEY = "loggedUser";

// getUser() — linhas 33–35
public SessionUser getUser(HttpSession session) {
    return (SessionUser) session.getAttribute(SESSION_KEY);
}

// setUser() — linhas 44–46
public void setUser(HttpSession session, SessionUser user) {
    session.setAttribute(SESSION_KEY, user);
}

// clearUser() — linhas 53–55
public void clearUser(HttpSession session) {
    session.removeAttribute(SESSION_KEY);
}

// isAuthenticated() — linhas 63–65
public boolean isAuthenticated(HttpSession session) {
    return session.getAttribute(SESSION_KEY) != null;
}
```

O `SessionService` é anotado com `@Service` (linha 22), o que o torna um bean Spring
injectável por `@Autowired`. O `SESSION_KEY = "loggedUser"` é a chave que tanto o
`SessionService` como o `AuthInterceptor` usam para aceder ao utilizador em sessão.

### 4.2 Protecção de Rotas — AuthInterceptor

#### 4.2.1 Implementação do HandlerInterceptor

**Ficheiro:** `src/main/java/com/example/webapp/config/AuthInterceptor.java` (61 linhas)

```java
// AuthInterceptor.java — linhas 30–59
@Component
public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);
    private static final String SESSION_KEY = "loggedUser";  // linha 35

    @Override
    public boolean preHandle(            // linhas 44–59
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws IOException {
        HttpSession session = request.getSession(false);  // linha 49; false = não criar sessão nova

        if (session != null && session.getAttribute(SESSION_KEY) != null) {
            return true; // Utilizador autenticado — prosseguir  // linha 52
        }

        log.debug("Acesso não autorizado a [{}] — a redirigir para /login",
                request.getRequestURI());
        response.sendRedirect(request.getContextPath() + "/login");
        return false;  // linha 58; interrompe o processamento do pedido
    }
}
```

O `HandlerInterceptor` do Spring é invocado antes de cada método de controller
(`preHandle`). Se retornar `false`, o pedido é interrompido sem chegar ao controller.
O `request.getSession(false)` é importante: passa `false` para **não criar** uma sessão
nova se não existir — apenas verificar a existente.

#### 4.2.2 Registo do Interceptor — WebMvcConfig

**Ficheiro:** `src/main/java/com/example/webapp/config/WebMvcConfig.java` (45 linhas)

```java
// WebMvcConfig.java — linhas 24–44
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")           // linha 33 — protege TUDO
                .excludePathPatterns(
                        "/",           // linha 35 — landing page pública
                        "/login",      // linha 36 — formulário de login (GET + POST)
                        "/logout",     // linha 37 — logout (POST) — o controller invalida a sessão
                        "/css/**",     // linha 38 — ficheiros CSS estáticos
                        "/js/**",      // linha 39 — ficheiros JavaScript estáticos
                        "/images/**",  // linha 40 — imagens estáticas
                        "/webjars/**"  // linha 41 — webjars
                );
    }
}
```

**Rotas públicas (sem autenticação):** `/`, `/login`, `/logout`, `/css/**`, `/js/**`,
`/images/**`, `/webjars/**`.

**Rotas protegidas (requerem sessão activa com `loggedUser`):** todas as outras, incluindo
`/catalogo`, `/produto/**`, `/carrinho`, `/checkout/**`, `/encomendas`, `/encomendas/**`,
`/encomenda/**`, `/client-area`.

### 4.3 Fluxo de Login — Passo a Passo Detalhado

#### Passo 1 — GET /login

**Ficheiro:** `src/main/java/com/example/webapp/controller/AuthController.java` (linhas 53–59)

```java
@GetMapping("/login")
public String loginPage(HttpSession session, Model model) {
    if (sessionService.isAuthenticated(session)) {
        return "redirect:/client-area";  // Já autenticado → redirige
    }
    return "login";  // Renderiza login.html
}
```

Se o utilizador já tiver sessão activa, é redirigido para `/client-area` em vez de ver o
formulário de login novamente.

#### Passo 2 — Utilizador submete POST /login

**Ficheiro:** `src/main/java/com/example/webapp/controller/AuthController.java` (linhas 77–83)

```java
@PostMapping("/login")
public String login(
        @RequestParam String email,     // linha 79 — campo "email" do formulário HTML
        @RequestParam String password,  // linha 80 — campo "password" do formulário HTML
        HttpSession session,
        Model model
) {
```

O template `login.html` (linha 56) submete para `/login` via `method="POST"`:

```html
<!-- login.html — linha 56 -->
<form th:action="@{/login}" action="/login" method="POST" class="space-y-5">
```

Os campos do formulário têm atributos `name="email"` (linha 64) e `name="password"` (linha 80),
que correspondem exactamente aos `@RequestParam`.

#### Passo 3 — AuthService.login() chamado sincronamente

**Ficheiro:** `src/main/java/com/example/webapp/service/AuthService.java` (linhas 41–50)

```java
public LoginResponse login(String email, String password) throws IOException {
    Response<LoginResponse> response =
            api.login(new LoginRequest(email, password)).execute();  // linha 43 — .execute() síncrono

    if (response.isSuccessful()) {
        return response.body();
    }
    // 401/403 — credenciais inválidas; o controller trata
    return null;
}
```

A chamada `.execute()` (e não `.enqueue()`) bloqueia a thread do servlet até receber a
resposta da API. Se as credenciais forem inválidas, a API devolve HTTP 4xx e `response.body()`
é `null` → o método retorna `null`. Se houver erro de rede, lança `IOException`.

#### Passo 4 — Sucesso: construção de SessionUser

**Ficheiro:** `src/main/java/com/example/webapp/controller/AuthController.java` (linhas 85–98)

```java
LoginResponse resp = authService.login(email, password);

if (resp == null || resp.role == null || !"CLIENTE".equalsIgnoreCase(resp.role)) {
    // Linha 87–92: credenciais inválidas ou role não autorizado
    model.addAttribute("error", "E-mail ou palavra-passe incorrectos ou não tem permissões...");
    model.addAttribute("email", email);
    return "login";
}

// Linha 96: construção do SessionUser
SessionUser user = new SessionUser(resp.id, resp.nome, resp.email, resp.role);
user.setToken(resp.token);            // linha 97: token JWT definido separadamente
sessionService.setUser(session, user); // linha 98: guardado na sessão HTTP
```

O sistema só permite login para utilizadores com `role == "CLIENTE"`. Qualquer outro role
(ex.: "ADMIN", "GESTOR") é rejeitado com a mesma mensagem de erro que credenciais inválidas.
Isto é uma decisão de segurança: o portal web é apenas para clientes B2B.

Os campos de `LoginResponse` são (ficheiro `LoginResponse.java`):
- `UUID id` — linha 11
- `String nome` — linha 12
- `String email` — linha 13
- `String role` — linha 14
- `String token` — linha 15

#### Passo 5 — Redirecionamento baseado no role

**Ficheiro:** `src/main/java/com/example/webapp/controller/AuthController.java` (linhas 103, 138–147)

```java
return redirectByRole(resp.role);  // linha 103

// linhas 138–147
private String redirectByRole(String role) {
    if (role == null) {
        return "redirect:/client-area";
    }
    return switch (role.toUpperCase()) {
        case "CLIENTE" -> "redirect:/client-area";
        // Outros roles poderão ser adicionados aqui à medida que o portal evolua
        default -> "redirect:/client-area";
    };
}
```

Actualmente, todos os roles redirecionam para `/client-area`. O `switch` está preparado
para expansão futura (ex.: "ADMIN" → `/admin-dashboard`).

#### Passo 6 — Falha: re-renderização com mensagem de erro

**AuthController.java** (linhas 87–92, falha de credenciais):

```java
model.addAttribute("error", "E-mail ou palavra-passe incorrectos ou não tem permissões para aceder a esta área.");
model.addAttribute("email", email);
return "login";
```

**AuthController.java** (linhas 105–110, falha de rede):

```java
catch (IOException e) {
    log.error("Erro de rede ao tentar autenticar: {}", e.getMessage(), e);
    model.addAttribute("error", "Erro de ligação ao servidor. Por favor, tente mais tarde.");
    model.addAttribute("email", email);
    return "login";
}
```

No template **login.html** (linhas 94–97), a mensagem de erro é exibida condicionalmente:

```html
<!-- login.html — linhas 94–97 -->
<div th:if="${error}" class="alert alert-error text-sm flex items-start gap-3 ... bg-red-50 text-red-700 ...">
    <i class="fa-solid fa-circle-exclamation mt-0.5 flex-shrink-0"></i>
    <span th:text="${error}">Erro de autenticação.</span>
</div>
```

O `th:if="${error}"` exibe o bloco apenas quando o modelo contém o atributo `error`.
O `th:value="${email}"` no campo email (login.html, linha 65) pré-preenche o e-mail
para que o utilizador não precise de o voltar a escrever.

**Nota sobre o padrão PRG:** No login, a falha **não faz redirect** — re-renderiza o
template directamente no POST. Isto é uma excepção documentada ao padrão PRG, aceitável
para formulários de login, para que a mensagem de erro e o e-mail pré-preenchido possam
ser passados via Model (o Model não sobrevive a um redirect).

### 4.4 Logout

**Ficheiro:** `src/main/java/com/example/webapp/controller/AuthController.java` (linhas 120–125)

```java
@PostMapping("/logout")
public String logout(HttpSession session) {
    sessionService.clearUser(session);  // linha 122: remove "loggedUser" da sessão
    session.invalidate();               // linha 123: invalida toda a sessão (apaga cookie)
    return "redirect:/login";           // linha 124: PRG para o formulário de login
}
```

O logout é feito via `POST` (não `GET`) para evitar CSRF simples via hiperligação. O botão
de logout no layout é um formulário `method="POST"` (layout.html, linhas 74–78):

```html
<!-- fragments/layout.html — linhas 74–78 -->
<form th:action="@{/logout}" method="POST">
    <button type="submit" class="btn btn-ghost w-full justify-start text-red-600 ...">
        <i class="fa-solid fa-arrow-right-from-bracket w-5"></i> Terminar Sessão
    </button>
</form>
```

O `session.invalidate()` apaga todos os atributos da sessão (incluindo o carrinho) e
invalida o cookie `JSESSIONID` no browser.

---

## 5. Carrinho de Compras em Sessão

### 5.1 Modelos do Carrinho

#### 5.1.1 CarrinhoItem

**Ficheiro:** `src/main/java/com/example/webapp/model/carrinho/CarrinhoItem.java` (77 linhas)

```java
// CarrinhoItem.java — linhas 11–50
public class CarrinhoItem {

    private UUID produtoId;         // linha 14 — UUID do produto final
    private String produtoNome;     // linha 17 — nome para apresentação no UI
    private UUID palletTipoId;      // linha 20 — UUID do tipo de pallet seleccionado
    private String palletTipoNome;  // linha 23 — nome do pallet para apresentação (não vai à API)
    private int quantidadePallets;  // linha 26 — número de pallets pretendidos
    private double precoUnitario;   // linha 29 — preço por pallet com IVA
    private double precoTotal;      // linha 32 — quantidadePallets * precoUnitario

    // Construtor com todos os campos — linhas 40–50
    public CarrinhoItem(UUID produtoId, String produtoNome,
                        UUID palletTipoId, String palletTipoNome,
                        int quantidadePallets, double precoUnitario, double precoTotal) { ... }
}
```

Getters e setters nas linhas 56–75.

#### 5.1.2 Carrinho

**Ficheiro:** `src/main/java/com/example/webapp/model/carrinho/Carrinho.java` (86 linhas)

```java
// Carrinho.java — linhas 14–84
public class Carrinho implements Serializable {   // linha 14

    private static final long serialVersionUID = 1L;   // linha 16

    private final List<CarrinhoItem> items = new ArrayList<>();  // linha 18

    // add() — linhas 29–32
    public void add(CarrinhoItem item) {
        if (item == null) throw new IllegalArgumentException("item não pode ser nulo");
        items.add(item);
    }

    // remove() — linhas 40–42
    public void remove(int index) {
        items.remove(index);
    }

    // clear() — linhas 47–49
    public void clear() {
        items.clear();
    }

    // getItems() — linhas 58–60 — devolve vista IMUTÁVEL
    public List<CarrinhoItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    // isEmpty() — linhas 65–67
    public boolean isEmpty() {
        return items.isEmpty();
    }

    // totalItens() — linhas 72–74
    public int totalItens() {
        return items.size();
    }

    // getTotalCarrinho() — linhas 80–84
    public double getTotalCarrinho() {
        return items.stream()
                .mapToDouble(CarrinhoItem::getPrecoTotal)
                .sum();
    }
}
```

O `Carrinho` implementa `Serializable` (linha 14) para que o Spring (ou um servidor de aplicações)
possa serializar a sessão HTTP quando necessário (ex.: para persistência de sessão em disco
ou em Redis). Sem `Serializable`, a sessão poderia falhar em cenários de clustering.

O método `getItems()` devolve `Collections.unmodifiableList(items)`, o que garante que
o template Thymeleaf não pode modificar a lista directamente — apenas lê.

### 5.2 CarrinhoService

**Ficheiro:** `src/main/java/com/example/webapp/service/CarrinhoService.java` (100 linhas)

```java
// CarrinhoService.java — linha 33
private static final String SESSION_KEY = "carrinho";
```

#### getCarrinho() — cria carrinho vazio se ausente

```java
// CarrinhoService.java — linhas 46–53
public Carrinho getCarrinho(HttpSession session) {
    Carrinho carrinho = (Carrinho) session.getAttribute(SESSION_KEY);
    if (carrinho == null) {
        carrinho = new Carrinho();
        session.setAttribute(SESSION_KEY, carrinho);
    }
    return carrinho;
}
```

#### adicionarItem() — padrão de save-back

```java
// CarrinhoService.java — linhas 61–65
public void adicionarItem(HttpSession session, CarrinhoItem item) {
    Carrinho carrinho = getCarrinho(session);
    carrinho.add(item);
    session.setAttribute(SESSION_KEY, carrinho);  // linha 64: GUARDA DE VOLTA na sessão
}
```

O "padrão save-back" é importante: depois de modificar o `Carrinho`, chama-se
`session.setAttribute(SESSION_KEY, carrinho)` novamente. Em servidores de aplicações com
replicação de sessão (cluster), o servidor só sabe que um atributo foi modificado se
`setAttribute` for chamado explicitamente. Sem esta chamada, modificações ao objecto
não seriam propagadas.

#### removerItem() — por índice 0-based

```java
// CarrinhoService.java — linhas 73–77
public void removerItem(HttpSession session, int index) {
    Carrinho carrinho = getCarrinho(session);
    carrinho.remove(index);
    session.setAttribute(SESSION_KEY, carrinho);
}
```

#### limparCarrinho()

```java
// CarrinhoService.java — linhas 84–88
public void limparCarrinho(HttpSession session) {
    Carrinho carrinho = getCarrinho(session);
    carrinho.clear();
    session.setAttribute(SESSION_KEY, carrinho);
}
```

#### contarItens() — para badge na navbar

```java
// CarrinhoService.java — linhas 96–98
public int contarItens(HttpSession session) {
    return getCarrinho(session).totalItens();
}
```

Este método é usado indirectamente: a navbar (`layout.html`, linha 65) acede directamente
ao atributo de sessão via `${session.carrinho.totalItens()}`.

### 5.3 Página de Detalhes do Produto

#### Passo 1 — GET /produto/{id}

**Ficheiro:** `src/main/java/com/example/webapp/controller/ProdutoDetalheController.java` (linhas 69–116)

```java
@GetMapping("/produto/{id}")
public String detalhe(
        @PathVariable String id,      // linha 71
        HttpSession session,
        Model model,
        RedirectAttributes redirectAttributes
) {
    // 1. Buscar produto por id — linhas 77–85
    ProdutoCatalogoResponse produto;
    try {
        produto = produtoCatalogoService.getById(id);
    } catch (ProdutoCatalogoService.ProdutoCatalogoException e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Produto não encontrado...");
        return "redirect:/catalogo";
    }

    if (produto == null) {
        redirectAttributes.addFlashAttribute("errorMessage", "Produto não encontrado.");
        return "redirect:/catalogo";
    }

    // 2. Buscar tipos de pallet — linhas 95–105
    List<PalletTipoResponse> palletTipos;
    try {
        PaginatedResponse<PalletTipoResponse> paginatedPallets =
                palletTipoService.getAll(0, 200, "nome", "asc");   // linha 98: page=0, size=200
        palletTipos = paginatedPallets.content != null
                ? paginatedPallets.content
                : List.of();
    } catch (PalletTipoService.PalletTipoException e) {
        palletTipos = List.of();
    }

    // 3. Carrinho (para badge na navbar) — linha 108
    Carrinho carrinho = carrinhoService.getCarrinho(session);

    // 4. Popular modelo — linhas 111–113
    model.addAttribute("produto",      produto);
    model.addAttribute("palletTipos",  palletTipos);
    model.addAttribute("carrinho",     carrinho);

    return "produto-detalhe";  // linha 115
}
```

O `size=200` na chamada a `palletTipoService.getAll(0, 200, "nome", "asc")` é propositado:
pretende-se obter **todos** os tipos de pallet de uma vez (assumindo que existem menos de 200),
evitando paginação no dropdown de seleção de pallet.

#### Passo 2 — produto-detalhe.html: painel de adição ao carrinho

**Ficheiro:** `src/main/resources/templates/produto-detalhe.html` (linhas 105–172)

```html
<!-- produto-detalhe.html — linhas 106–109 -->
<form th:action="@{/produto/{id}/adicionar-carrinho(id=${produto.id})}"
      method="post"
      id="form-carrinho"
      class="space-y-4">

    <!-- Hidden: nome do produto — linha 112 -->
    <input type="hidden" name="produtoNome" th:value="${produto.nome}">

    <!-- Select de tipos de pallet — linhas 129–140 -->
    <select id="pallet-select"
            name="palletTipoId"
            required
            class="select select-bordered ..."
            onchange="updatePalletNome(this)">
        <option value="" disabled selected>Seleccione um tipo...</option>
        <option th:each="pt : ${palletTipos}"
                th:value="${pt.id}"
                th:text="${pt.nome + (pt.capacidadeKg != null ? ' (' + #numbers.formatDecimal(pt.capacidadeKg, 1, 0) + ' kg)' : '')}"
                th:data-nome="${pt.nome}">
        </option>
    </select>

    <!-- Hidden: nome do pallet para apresentação no carrinho — linha 142 -->
    <input type="hidden" id="pallet-tipo-nome" name="palletTipoNome" value="">

    <!-- Input de quantidade — linhas 152–158 -->
    <input id="quantidade"
           type="number"
           name="quantidadePallets"
           min="1"
           value="1"
           required
           class="input input-bordered ...">

    <!-- Botão submit — linhas 164–170 -->
    <button type="submit"
            id="btn-adicionar-carrinho"
            th:disabled="${palletTipos == null or palletTipos.isEmpty()}"
            class="btn w-full bg-blue-600 ...">
        <i class="fa-solid fa-cart-plus"></i>
        Adicionar ao Carrinho
    </button>
</form>
```

O atributo `th:data-nome="${pt.nome}"` no `<option>` guarda o nome do pallet como
`data-*` attribute HTML. O JavaScript (linha 197–203) sincroniza esse valor para o
`<input hidden>` quando o utilizador muda a selecção.

#### Passo 3 — JavaScript vanilla: sincronização do nome do pallet

**Ficheiro:** `src/main/resources/templates/produto-detalhe.html` (linhas 195–204)

```html
<script>
    // produto-detalhe.html — linhas 197–203
    function updatePalletNome(selectEl) {
        const selectedOption = selectEl.options[selectEl.selectedIndex];
        const nomeInput = document.getElementById('pallet-tipo-nome');
        if (nomeInput && selectedOption) {
            nomeInput.value = selectedOption.dataset.nome || selectedOption.text;
        }
    }
</script>
```

Este script não tem acesso a expressões Thymeleaf (como `[[${produto.nome}]]`) porque
os scripts `<script>` são processados pelo browser, não pelo Thymeleaf. O Thymeleaf
**não executa** expressões dentro de blocos `<script>` (sem configuração especial),
e tentar fazê-lo causaria `TemplateProcessingException`. A solução correcta é guardar
os dados do servidor em `data-*` attributes no HTML (processados pelo Thymeleaf como
atributos normais) e lê-los em JS via `element.dataset`.

### 5.4 POST /produto/{id}/adicionar-carrinho

**Ficheiro:** `src/main/java/com/example/webapp/controller/ProdutoDetalheController.java` (linhas 122–185)

```java
@PostMapping("/produto/{id}/adicionar-carrinho")
public String adicionarAoCarrinho(
        @PathVariable String id,
        @RequestParam UUID palletTipoId,      // linha 125: id do tipo de pallet
        @RequestParam String palletTipoNome,  // linha 126: nome (do hidden input)
        @RequestParam int quantidadePallets,  // linha 127: quantidade
        @RequestParam String produtoNome,     // linha 128: nome do produto (do hidden input)
        HttpSession session,
        RedirectAttributes redirectAttributes
) {
    // 1. Buscar produto para obter precoPorKg e taxaIva — linhas 133–140
    ProdutoCatalogoResponse produto;
    try {
        produto = produtoCatalogoService.getById(id);
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Erro ao adicionar produto: não encontrado.");
        return "redirect:/catalogo";
    }

    // 2. Buscar capacidade da pallet — linhas 142–159
    double capacidadePalletKg = 1.0; // fallback se não encontrar
    try {
        PaginatedResponse<PalletTipoResponse> paginatedPallets =
                palletTipoService.getAll(0, 200, "nome", "asc");
        if (paginatedPallets.content != null) {
            for (PalletTipoResponse pt : paginatedPallets.content) {
                if (pt.id != null && pt.id.equals(palletTipoId.toString())) {
                    if (pt.capacidadeKg != null) {
                        capacidadePalletKg = pt.capacidadeKg.doubleValue();
                    }
                    break;
                }
            }
        }
    } catch (Exception e) { /* fallback: 1kg */ }

    // 3. Calcular preço total com IVA — linhas 163–168
    double precoBase = produto.precoPorKg != null ? produto.precoPorKg.doubleValue() : 0.0;
    double taxaIva = produto.taxaIva != null ? produto.taxaIva.doubleValue() : 0.0;

    double precoUnitarioBase = precoBase * capacidadePalletKg;
    double precoUnitario = precoUnitarioBase * (1.0 + (taxaIva / 100.0));
    double precoTotal = precoUnitario * quantidadePallets;

    // 4. Construir CarrinhoItem — linhas 170–178
    CarrinhoItem item = new CarrinhoItem(
            UUID.fromString(id),
            produtoNome,
            palletTipoId,
            palletTipoNome,
            quantidadePallets,
            precoUnitario,
            precoTotal
    );
    carrinhoService.adicionarItem(session, item);   // linha 179

    // 5. PRG — linha 184
    return "redirect:/produto/" + id;
}
```

**Fórmula de cálculo de preço:**

```
precoUnitarioBase = precoPorKg × capacidadeKg
precoUnitario     = precoUnitarioBase × (1 + taxaIva / 100)
precoTotal        = precoUnitario × quantidadePallets
```

O PRG (`return "redirect:/produto/" + id`) garante que um `F5` no browser não re-submete
o formulário — redirige para o GET que é seguro de repetir.

### 5.5 Página do Carrinho

**Ficheiro controller:** `src/main/java/com/example/webapp/controller/CarrinhoController.java` (linhas 41–46)

```java
@GetMapping("/carrinho")
public String carrinho(HttpSession session, Model model) {
    Carrinho carrinho = carrinhoService.getCarrinho(session);
    model.addAttribute("carrinho", carrinho);
    return "carrinho";
}
```

**Ficheiro template:** `src/main/resources/templates/carrinho.html`

Iteração dos itens (linhas 67–109):

```html
<!-- carrinho.html — linhas 67–109 -->
<tr th:each="item, stat : ${carrinho.items}"
    class="hover:bg-slate-50/40 transition-colors ...">

    <!-- Produto com link — linhas 72–74 -->
    <td>
        <a th:href="@{/produto/{id}(id=${item.produtoId})}" class="font-bold ...">
            <span th:text="${item.produtoNome}">Nome</span>
        </a>
    </td>

    <!-- Tipo de Pallet — linhas 78–80 -->
    <td class="text-slate-600 text-sm" th:text="${item.palletTipoNome}">Tipo Pallet</td>

    <!-- Quantidade — linhas 83–87 -->
    <td class="text-center">
        <div class="inline-block bg-slate-100 border ...">
            <span th:text="${item.quantidadePallets}">1</span>
        </div>
    </td>

    <!-- Preço Unitário — linhas 90–92 -->
    <td class="text-right text-sm font-medium text-slate-500">
        <span th:text="${#numbers.formatDecimal(item.precoUnitario, 1, 2) + ' €'}">0,00 €</span>
    </td>

    <!-- Subtotal — linhas 95–97 -->
    <td class="text-right font-extrabold text-slate-800">
        <span th:text="${#numbers.formatDecimal(item.precoTotal, 1, 2) + ' €'}">0,00 €</span>
    </td>

    <!-- Remover — linhas 100–108 -->
    <td class="w-16 text-right">
        <form th:action="@{/carrinho/remover/{index}(index=${stat.index})}" method="post">
            <button type="submit" class="btn btn-sm btn-circle btn-ghost text-red-500 ...">
                <i class="fa-solid fa-trash-can"></i>
            </button>
        </form>
    </td>
</tr>
```

O `stat` em `th:each="item, stat : ${carrinho.items}"` é uma variável de estado do loop
que expõe propriedades como `stat.index` (índice 0-based), `stat.count`, `stat.first`,
`stat.last`. O `stat.index` é passado na URL de remoção.

**Botão "Finalizar Encomenda"** (carrinho.html, linhas 144–150):

```html
<!-- carrinho.html — linhas 144–150 -->
<form th:action="@{/carrinho/finalizar}" method="post">
    <button type="submit" class="btn btn-primary w-full mt-6 ...">
        Finalizar Encomenda
        <i class="fa-solid fa-arrow-right ml-2 ..."></i>
    </button>
</form>
```

### 5.6 Checkout — POST /carrinho/finalizar

**Ficheiro:** `src/main/java/com/example/webapp/controller/CarrinhoController.java` (linhas 63–99)

#### Passo 1 — Obter utilizador da sessão

```java
// CarrinhoController.java — linhas 64–68
SessionUser user = sessionService.getUser(session);
if (user == null) {
    return "redirect:/login";
}
```

#### Passo 2 — Obter carrinho e verificar se está vazio

```java
// CarrinhoController.java — linhas 70–73
Carrinho carrinho = carrinhoService.getCarrinho(session);
if (carrinho == null || carrinho.isEmpty()) {
    return "redirect:/carrinho";
}
```

#### Passo 3 — Obter moeda EUR

```java
// CarrinhoController.java — linha 76
MoedaResponse moeda = moedaService.getByCodigo("EUR");
```

`MoedaService.getByCodigo()` (ficheiro `MoedaService.java`, linhas 17–24):

```java
public MoedaResponse getByCodigo(String codigo) throws Exception {
    Response<MoedaResponse> response = moedaApiService.getByCodigo(codigo).execute();
    if (response.isSuccessful() && response.body() != null) {
        return response.body();
    } else {
        throw new Exception("Falha ao obter moeda por código: " + codigo + ". HTTP " + response.code());
    }
}
```

A interface Retrofit usada é `IMoedaApiService.java` (linha 10–11):

```java
@GET("moedas/codigo/{codigo}")
Call<MoedaResponse> getByCodigo(@Path("codigo") String codigo);
```

#### Passo 4 — Construir CreateEncomendaRequest

```java
// CarrinhoController.java — linhas 78–89
List<EncomendaPalletItem> pallets = carrinho.getItems().stream()
        .map(item -> new EncomendaPalletItem(
                item.getProdutoId().toString(),
                item.getPalletTipoId().toString(),
                item.getQuantidadePallets()))
        .collect(Collectors.toList());

CreateEncomendaRequest request = new CreateEncomendaRequest(
        user.getId().toString(),   // userId (String) da sessão
        moeda.id,                  // moedaId (String) da resposta da API
        pallets                    // List<EncomendaPalletItem>
);
```

**Estrutura de CreateEncomendaRequest** (ficheiro `CreateEncomendaRequest.java`):
- `String userId` — linha 11
- `String moedaId` — linha 12
- `List<EncomendaPalletItem> pallets` — linha 13

**Estrutura de EncomendaPalletItem** (ficheiro `EncomendaPalletItem.java`):
- `String produtoId` — linha 8
- `String palletTipoId` — linha 9
- `int quantidadePallets` — linha 10

#### Passo 5 — POST /encomendas via EncomendaService

```java
// CarrinhoController.java — linha 91
encomendaService.criar(request);
```

`EncomendaService.criar()` (ficheiro `EncomendaService.java`, linhas 19–24):

```java
public void criar(CreateEncomendaRequest request) throws Exception {
    Response<Object> response = api.criar(request).execute();  // .execute() síncrono
    if (!response.isSuccessful()) {
        throw new Exception("Falha ao criar encomenda. HTTP " + response.code());
    }
}
```

#### Passo 6 — Sucesso

```java
// CarrinhoController.java — linhas 93–94
carrinhoService.limparCarrinho(session);
return "redirect:/checkout";
```

#### Passo 7 — Erro

```java
// CarrinhoController.java — linhas 96–98
} catch (Exception e) {
    return "redirect:/checkout-error";
}
```

### 5.7 Páginas de Resultado do Checkout

#### checkout.html

**Ficheiro:** `src/main/resources/templates/checkout.html` (30 linhas)

Usa o layout partilhado (`th:replace` na linha 3). Exibe um ícone verde de check,
a mensagem "Encomenda realizada com sucesso!", e dois botões:
- "Ver as minhas encomendas" → `@{/encomendas}` (linha 18)
- "Voltar ao catálogo" → `@{/catalogo}` (linha 21)

**Controller:** `CarrinhoController.java`, linhas 101–104:
```java
@GetMapping("/checkout")
public String checkoutSuccess(Model model) {
    return "checkout";
}
```

#### checkout-error.html

**Ficheiro:** `src/main/resources/templates/checkout-error.html` (30 linhas)

Exibe um ícone vermelho de triângulo de exclamação, a mensagem de erro, e dois botões:
- "Voltar ao carrinho" → `@{/carrinho}` (linha 18)
- "Ir para o catálogo" → `@{/catalogo}` (linha 21)

**Controller:** `CarrinhoController.java`, linhas 106–109:
```java
@GetMapping("/checkout-error")
public String checkoutError(Model model) {
    return "checkout-error";
}
```

---

## 6. Padrão de Páginas Server-Rendered

### 6.1 Layout e Fragmentos Thymeleaf

#### 6.1.1 Layout partilhado — fragments/layout.html

**Ficheiro:** `src/main/resources/templates/fragments/layout.html` (134 linhas)

O layout é definido como um **fragmento parametrizado** na linha 2:

```html
<!-- fragments/layout.html — linha 2 -->
<html lang="pt" xmlns:th="http://www.thymeleaf.org"
      th:fragment="layout(title, activePage, content)">
```

Aceita três parâmetros:
- `title` — título da página (para `<title>` no `<head>`)
- `activePage` — identificador da página activa (para realçar o item de menu activo)
- `content` — o fragmento de conteúdo a injectar

**Inclusão do layout numa página filha** (ex.: catalogo.html, linha 2–3):

```html
<html lang="pt" xmlns:th="http://www.thymeleaf.org"
      th:replace="~{fragments/layout :: layout(
          title='Catálogo | Área B2B',
          activePage='catalogo',
          content=~{::#main-content})}">
```

`th:replace="~{fragments/layout :: layout(...)}"` substitui o elemento `<html>` inteiro
pelo fragmento `layout` do ficheiro `fragments/layout.html`, passando os parâmetros.
`content=~{::#main-content}` selecciona o elemento com `id="main-content"` do próprio
ficheiro como conteúdo a injectar.

**Injecção do conteúdo** (layout.html, linhas 127–129):

```html
<!-- fragments/layout.html — linhas 127–129 -->
<div class="flex-1 p-6 md:p-8 lg:p-10 overflow-y-auto bg-slate-50 w-full">
    <div th:replace="${content}"></div>
</div>
```

`th:replace="${content}"` injeta o fragmento de conteúdo passado como parâmetro.

#### 6.1.2 Sidebar e Navegação no Layout

O layout inclui uma sidebar para desktop e uma navbar para mobile (layout.html, linhas 40–122).

**Menu item com estado activo** (layout.html, linhas 51–54):

```html
<!-- fragments/layout.html — linhas 51–54 -->
<li>
    <a th:href="@{/client-area}"
       th:classappend="${activePage == 'dashboard'} ? 'bg-blue-50 text-blue-700 font-bold' : 'hover:bg-slate-50'">
        <i class="fa-solid fa-chart-pie w-5"></i> Dashboard
    </a>
</li>
```

`th:classappend` adiciona classes CSS dinamicamente com base no valor de `activePage`.
Se a página activa for `'dashboard'`, adiciona `bg-blue-50 text-blue-700 font-bold`
(fundo azul claro, texto azul negrito); caso contrário, apenas `hover:bg-slate-50`.

**Badge do carrinho na navbar** (layout.html, linhas 65–70):

```html
<!-- fragments/layout.html — linhas 65–70 -->
<li th:if="${session.carrinho != null and session.carrinho.totalItens() > 0}">
    <a th:href="@{/carrinho}"
       th:classappend="${activePage == 'carrinho'} ? 'bg-blue-50 text-blue-700 font-bold' : 'hover:bg-slate-50 text-blue-600'">
        <i class="fa-solid fa-cart-shopping w-5"></i> Carrinho
        <span class="badge badge-sm badge-primary ml-auto"
              th:text="${session.carrinho.totalItens()}">0</span>
    </a>
</li>
```

`${session.carrinho}` acede directamente ao atributo `"carrinho"` da sessão HTTP a partir
do template Thymeleaf. Isto é diferente de aceder via Model — o Thymeleaf expõe a sessão
através do objecto `session`.

#### 6.1.3 Dependências externas carregadas no layout

O `<head>` do layout (linhas 3–36) carrega todos os recursos externos:

```html
<!-- fragments/layout.html — linhas 8–17 -->
<script src="https://cdn.tailwindcss.com"></script>
<link href="https://cdn.jsdelivr.net/npm/daisyui@4.12.10/dist/full.min.css" rel="stylesheet" />
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
<link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@300;400;600;700;800&display=swap" rel="stylesheet">
<!-- CSS Customizado -->
<link rel="stylesheet" th:href="@{/css/dashboard.css}">
```

A configuração do Tailwind CSS é feita inline (linhas 18–36):

```html
<script>
    tailwind.config = {
        theme: {
            extend: {
                fontFamily: {
                    sans: ['Plus Jakarta Sans', 'sans-serif'],
                },
                keyframes: {
                    shimmer: {
                        '100%': { transform: 'translateX(100%)' }
                    }
                }
            }
        },
        daisyui: {
            themes: ["light"],
        }
    }
</script>
```

### 6.2 CSS Customizado — dashboard.css

**Ficheiro:** `src/main/resources/static/css/dashboard.css` (23 linhas)

```css
/* dashboard.css — linhas 6–22 */
::-webkit-scrollbar {
    width: 6px;
    height: 6px;
}

::-webkit-scrollbar-track {
    background: #f8fafc; /* slate-50 */
}

::-webkit-scrollbar-thumb {
    background: #cbd5e1; /* slate-300 */
    border-radius: 10px;
}

::-webkit-scrollbar-thumb:hover {
    background: #94a3b8; /* slate-400 */
}
```

O CSS customizado é mínimo: apenas personalização da barra de scroll para uma aparência
mais elegante e consistente com a paleta de cores Slate do Tailwind CSS.

### 6.3 Padrão de Paginação em Thymeleaf

#### Exemplo completo do catalogo.html

O mesmo padrão de paginação é replicado em `catalogo.html` e `encomendas.html`.

**Selector de tamanho de página:**

```html
<!-- catalogo.html — linhas 15–27 -->
<form method="get" action="/catalogo" class="flex items-center gap-2">
    <label for="page-size-select" class="...">Por página:</label>
    <select id="page-size-select" name="size"
            onchange="this.form.submit()"
            class="select select-bordered ...">
        <option th:each="opt : ${pageSizeOptions}"
                th:value="${opt}"
                th:text="${opt}"
                th:selected="${opt == pageSize}">10</option>
    </select>
    <input type="hidden" name="page" value="0">
</form>
```

`th:selected="${opt == pageSize}"` usa comparação de igualdade Thymeleaf para marcar
a opção actualmente seleccionada com o atributo HTML `selected`.
`onchange="this.form.submit()"` submete o formulário ao alterar a selecção, sem necessidade
de um botão de submissão.

**Barra de navegação de páginas:**

```html
<!-- catalogo.html — linhas 133–155 -->
<div class="join shadow-sm">
    <!-- Botão Anterior activo — linhas 135–139 -->
    <a th:if="${!produtos.first}"
       th:href="@{/catalogo(page=${currentPage - 1}, size=${pageSize})}"
       class="join-item btn btn-sm ...">
        <i class="fa-solid fa-chevron-left mr-1"></i> Anterior
    </a>

    <!-- Botão Anterior desactivado (primeira página) — linhas 140–143 -->
    <span th:if="${produtos.first}"
          class="join-item btn btn-sm ... pointer-events-none">
        <i class="fa-solid fa-chevron-left mr-1"></i> Anterior
    </span>

    <!-- Botão Próxima activo — linhas 146–150 -->
    <a th:if="${!produtos.last}"
       th:href="@{/catalogo(page=${currentPage + 1}, size=${pageSize})}"
       class="join-item btn btn-sm ...">
        Próxima <i class="fa-solid fa-chevron-right ml-1"></i>
    </a>

    <!-- Botão Próxima desactivado (última página) — linhas 151–154 -->
    <span th:if="${produtos.last}"
          class="join-item btn btn-sm ... pointer-events-none">
        Próxima <i class="fa-solid fa-chevron-right ml-1"></i>
    </span>
</div>
```

A expressão `th:href="@{/catalogo(page=${currentPage - 1}, size=${pageSize})}"` constrói
o URL com parâmetros de query string: `/catalogo?page=0&size=10`, por exemplo. O Thymeleaf
gere automaticamente a codificação dos parâmetros.

### 6.4 EnumDisplayHelper (versão web)

**Ficheiro:** `src/main/java/com/example/webapp/util/EnumDisplayHelper.java` (91 linhas)

```java
// EnumDisplayHelper.java — linha 24
@Component("enumDisplayHelper")
public class EnumDisplayHelper {
```

A anotação `@Component("enumDisplayHelper")` regista a classe como bean Spring com o nome
`"enumDisplayHelper"`. O Thymeleaf expõe todos os beans Spring com a sintaxe `${@beanName}`.

#### Categorias de enums cobertas

**1. EstadoFísico (produtos)** — linhas 29–46:

```java
// linhas 32–34
ESTADO_FISICO.put("LIQUIDO", "Líquido");
ESTADO_FISICO.put("SOLIDO",  "Sólido");

public String getEstadoFisicoLabel(String valor) {  // linha 43
    if (valor == null) return "—";
    return ESTADO_FISICO.getOrDefault(valor, valor);
}
```

**2. EstadoEncomenda** — linhas 50–67:

```java
// linhas 53–56
ESTADO_ENCOMENDA.put("PENDENTE",  "Pendente");
ESTADO_ENCOMENDA.put("EXPEDIDA",  "Expedida");
ESTADO_ENCOMENDA.put("CANCELADA", "Cancelada");

public String getEstadoEncomendaLabel(String valor) {  // linha 64
```

**3. EstadoEncomendaMp** — linhas 69–89:

```java
// linhas 73–77
ESTADO_ENCOMENDA_MP.put("PENDENTE",    "Pendente");
ESTADO_ENCOMENDA_MP.put("ENCOMENDADA", "Encomendada");
ESTADO_ENCOMENDA_MP.put("RECEBIDA",    "Recebida");
ESTADO_ENCOMENDA_MP.put("CANCELADA",   "Cancelada");

public String getEstadoEncomendaMpLabel(String valor) {  // linha 86
```

**Chamada a partir de templates Thymeleaf:**

```html
<!-- catalogo.html — linhas 92–95 -->
<span th:with="label=${@enumDisplayHelper.getEstadoFisicoLabel(produto.estadoFisico)}"
      th:text="${label}"
      th:classappend="${produto.estadoFisico == 'LIQUIDO'} ? 'badge badge-info badge-outline' : 'badge badge-warning badge-outline'"
      class="badge font-medium">—</span>
```

A sintaxe `th:with="label=..."` cria uma variável local `label` para evitar chamar
o método duas vezes. O resultado do método é uma string em português (ex.: "Líquido")
em vez da constante da API (ex.: "LIQUIDO").

---

## 7. Gestão de Encomendas do Cliente (Web)

### 7.1 Listagem com Estatísticas — MinhasEncomendasController

**Ficheiro:** `src/main/java/com/example/webapp/controller/MinhasEncomendasController.java` (80 linhas)

```java
// MinhasEncomendasController.java — linhas 32–78
@GetMapping("/encomendas")
public String minhasEncomendas(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        HttpSession session,
        Model model
) {
    SessionUser user = sessionService.getUser(session);  // linha 39

    // Calcular estatísticas — duas chamadas à API
    // 1ª: para estatísticas totais (page=0, size=1000) — linhas 46–57
    long totalPendentes = 0;
    long totalExpedidas = 0;
    long totalCanceladas = 0;

    try {
        PaginatedResponse<EncomendaResumoResponse> statsResponse =
                encomendaService.getByUserId(user.getId(), 0, 1000);  // linha 47
        if (statsResponse.content != null) {
            for (EncomendaResumoResponse enc : statsResponse.content) {
                if ("PENDENTE".equals(enc.estado)) totalPendentes++;
                else if ("EXPEDIDA".equals(enc.estado)) totalExpedidas++;
                else if ("CANCELADA".equals(enc.estado)) totalCanceladas++;
            }
        }
    } catch (Exception e) { /* ignora */ }

    // 2ª: para a página corrente — linhas 59–66
    PaginatedResponse<EncomendaResumoResponse> encomendas;
    try {
        encomendas = encomendaService.getByUserId(user.getId(), page, size);  // linha 61
    } catch (Exception e) {
        encomendas = new PaginatedResponse<>();
        model.addAttribute("errorMessage", "Não foi possível carregar as encomendas.");
    }

    // Popular modelo — linhas 68–75
    model.addAttribute("encomendas",       encomendas);
    model.addAttribute("currentPage",      page);
    model.addAttribute("pageSize",         size);
    model.addAttribute("pageSizeOptions",  PAGE_SIZE_OPTIONS);  // List.of(10, 20, 50) — linha 24
    model.addAttribute("totalPendentes",   totalPendentes);
    model.addAttribute("totalExpedidas",   totalExpedidas);
    model.addAttribute("totalCanceladas",  totalCanceladas);

    return "encomendas";  // linha 77
}
```

**Nota importante:** São feitas **duas chamadas à API** por cada pedido à página:
1. Uma com `size=1000` para calcular as estatísticas sobre todas as encomendas.
2. Uma com o `page` e `size` reais para obter a página a exibir.

A lógica de contagem é feita no controller em Java, iterando sobre `statsResponse.content`.

#### Template encomendas.html — cards de estatísticas

**Ficheiro:** `src/main/resources/templates/encomendas.html` (linhas 28–55)

```html
<!-- encomendas.html — linhas 28–55: stats cards com daisyUI -->
<div class="stats stats-vertical lg:stats-horizontal shadow-sm border border-slate-100 w-full bg-white rounded-2xl">

    <div class="stat">
        <div class="stat-figure text-slate-500 bg-slate-50 p-3 rounded-xl">
            <i class="fa-solid fa-clock-rotate-left fa-xl"></i>
        </div>
        <div class="stat-title font-semibold text-slate-500">Pendentes</div>
        <div class="stat-value text-slate-800 text-4xl" th:text="${totalPendentes}">0</div>
    </div>

    <div class="stat">
        <div class="stat-figure text-blue-500 bg-blue-50 p-3 rounded-xl">
            <i class="fa-solid fa-truck-fast fa-xl"></i>
        </div>
        <div class="stat-title font-semibold text-slate-500">Expedidas</div>
        <div class="stat-value text-slate-800 text-4xl" th:text="${totalExpedidas}">0</div>
    </div>

    <div class="stat">
        <div class="stat-figure text-red-500 bg-red-50 p-3 rounded-xl">
            <i class="fa-solid fa-xmark fa-xl"></i>
        </div>
        <div class="stat-title font-semibold text-slate-500">Canceladas</div>
        <div class="stat-value text-slate-800 text-4xl" th:text="${totalCanceladas}">0</div>
    </div>
</div>
```

Os valores `${totalPendentes}`, `${totalExpedidas}`, `${totalCanceladas}` são populados
pelo controller como atributos do Model.

#### Tabela de encomendas com estado pill colorido

```html
<!-- encomendas.html — linhas 102–122 -->
<tr th:each="encomenda : ${encomendas.content}"
    class="hover:bg-slate-50/60 transition-colors ...">

    <!-- Data — linha 103–105 -->
    <td>
        <span class="font-semibold text-slate-800"
              th:text="${#strings.substring(encomenda.dataEncomenda, 0, 10)}">—</span>
    </td>

    <!-- Estado com badge colorido — linhas 106–111 -->
    <td>
        <span th:with="label=${@enumDisplayHelper.getEstadoEncomendaLabel(encomenda.estado)}"
              th:text="${label}"
              th:classappend="${encomenda.estado == 'EXPEDIDA'} ? 'badge badge-success badge-outline'
                             : (${encomenda.estado == 'CANCELADA'} ? 'badge badge-error badge-outline'
                             : 'badge badge-warning badge-outline')"
              class="badge font-medium">—</span>
    </td>

    <!-- Total — linhas 112–115 -->
    <td class="text-right">
        <span class="font-bold text-slate-800"
              th:text="${encomenda.totalPrecoEur != null ?
                         #numbers.formatDecimal(encomenda.totalPrecoEur, 1, 2) + ' €' : '—'}">—</span>
    </td>

    <!-- Link para detalhe — linhas 116–121 -->
    <td class="text-center">
        <a th:href="@{/encomendas/{id}(id=${encomenda.id})}"
           class="btn btn-sm bg-blue-50 text-blue-700 ...">
            <i class="fa-solid fa-eye mr-1"></i> Detalhes
        </a>
    </td>
</tr>
```

`#strings.substring(encomenda.dataEncomenda, 0, 10)` extrai apenas os primeiros 10 caracteres
de `dataEncomenda` (que é uma `String` no modelo), obtendo a data no formato `YYYY-MM-DD`.

### 7.2 Detalhes da Encomenda

**Ficheiro:** `src/main/java/com/example/webapp/controller/EncomendaDetalheController.java` (linhas 29–55)

```java
@GetMapping("/encomendas/{id}")
public String detalhe(
        @PathVariable String id,
        HttpSession session,
        Model model,
        RedirectAttributes redirectAttributes
) {
    SessionUser user = sessionService.getUser(session);

    EncomendaDetalheResponse encomenda;
    try {
        encomenda = encomendaService.getById(id);
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Encomenda não encontrada.");
        return "redirect:/encomendas";
    }

    // Verificação de propriedade — linha 47
    if (encomenda == null || !user.getId().toString().equals(encomenda.userId)) {
        log.warn("Acesso negado à encomenda [id={}] pelo user [userId={}]", id, user.getId());
        return "redirect:/encomendas";
    }

    model.addAttribute("encomenda", encomenda);
    return "encomenda-detalhe";
}
```

A verificação de propriedade na linha 47 (`!user.getId().toString().equals(encomenda.userId)`)
é uma camada de segurança adicional: mesmo que um utilizador tente aceder ao URL de uma
encomenda que não é sua (ex.: `/encomendas/abc-def-ghi`), é redirecionado para a listagem.
A API REST pode já fazer esta verificação, mas a web app faz-a também por defesa em profundidade.

### 7.3 Cancelamento de Encomenda

**Ficheiro:** `src/main/java/com/example/webapp/controller/EncomendaDetalheController.java` (linhas 57–81)

```java
@PostMapping("/encomenda/{id}/cancelar")
public String cancelar(@PathVariable String id, HttpSession session) {
    SessionUser user = sessionService.getUser(session);

    // Verifica que a encomenda existe e pertence ao utilizador — linhas 61–72
    EncomendaDetalheResponse encomenda;
    try {
        encomenda = encomendaService.getById(id);
    } catch (Exception e) {
        return "redirect:/encomendas";
    }

    if (encomenda == null || !user.getId().toString().equals(encomenda.userId)) {
        return "redirect:/encomendas";
    }

    // Cancelar via API — linhas 74–80
    try {
        encomendaService.cancelar(id);
        return "redirect:/encomenda-cancelada-sucesso";
    } catch (Exception e) {
        log.error("Erro ao cancelar a encomenda [id={}]", id, e);
        return "redirect:/encomenda-cancelada-erro";
    }
}
```

O botão de cancelamento só é visível quando o estado é "PENDENTE" (encomenda-detalhe.html, linha 31):

```html
<!-- encomenda-detalhe.html — linha 31 -->
<div th:if="${encomenda.estado == 'PENDENTE'}">
    <button onclick="document.getElementById('cancel_modal').showModal()"
            class="btn btn-error btn-sm text-white ...">
        <i class="fa-solid fa-xmark mr-1"></i> Cancelar Encomenda
    </button>

    <!-- Modal de confirmação daisyUI (linhas 38–61) -->
    <dialog id="cancel_modal" class="modal modal-bottom sm:modal-middle">
        <div class="modal-box bg-white shadow-xl">
            <!-- ... -->
            <div class="modal-action flex items-center gap-2">
                <!-- Botão Voltar (fecha modal) -->
                <form method="dialog" class="m-0 p-0">
                    <button class="btn btn-ghost ...">Voltar</button>
                </form>
                <!-- Botão Confirmar (submete POST) -->
                <form th:action="@{/encomenda/{id}/cancelar(id=${encomenda.id})}" method="post" class="m-0 p-0">
                    <button type="submit" class="btn btn-error text-white ...">
                        <i class="fa-solid fa-check mr-1"></i> Confirmar
                    </button>
                </form>
            </div>
        </div>
    </dialog>
</div>
```

Em vez do `confirm()` nativo do browser, usa-se um modal daisyUI (`<dialog>`) que é
mais moderno, não bloqueante, e visualmente consistente com o resto da interface. O botão
"Cancelar Encomenda" chama `document.getElementById('cancel_modal').showModal()` para
abrir o modal. Dentro do modal, o formulário `POST /encomenda/{id}/cancelar` só é submetido
quando o utilizador clica "Confirmar".

`EncomendaService.cancelar()` (ficheiro `EncomendaService.java`, linhas 42–47):

```java
public void cancelar(String id) throws Exception {
    Response<okhttp3.ResponseBody> response = api.cancelar(id).execute();
    if (!response.isSuccessful()) {
        throw new Exception("Falha ao cancelar a encomenda. HTTP " + response.code());
    }
}
```

Interface Retrofit usada: `IEncomendaApiService.java`, linha 32–33:
```java
@PATCH("encomendas/{id}/cancelar")
Call<ResponseBody> cancelar(@Path("id") String id);
```

---

## 8. Módulos Implementados — Descrição de Todas as Páginas

### Página 1 — Landing Page (/)

**URL:** `/`
**Controller:** `src/main/java/com/example/webapp/controller/WebController.java` (linhas 20–23)
**Template:** `src/main/resources/templates/index.html` (434 linhas)

```java
// WebController.java — linhas 20–23
@GetMapping("/")
public String index() {
    return "index";
}
```

A landing page é uma página de marketing **pública** (não requer autenticação). O `AuthInterceptor`
exclui `/` da verificação de sessão (`WebMvcConfig.java`, linha 35).

**Conteúdo da página (estrutura por secções):**

1. **Navbar** (linhas 33–72): Logo "Norgurtes", links de navegação para secções da página,
   botão "Área de Cliente" que aponta para `/login`.

2. **Hero Section** (linhas 74–126): Secção principal com headline "O iogurte fresco que
   valoriza o seu negócio.", sub-título, botões CTA, métricas B2B (100% Leite Nacional, +250
   Clientes Activos, 24h Entrega Fria), e imagem de produto.

3. **Sobre Nós** (linhas 128–174): id `#sobre`. Texto sobre a fábrica, certificações.

4. **Canais de Venda** (linhas 176–220): id `#parceiros`. Quatro cards: Supermercados,
   Cafés & Pastelarias, Restauração & Horeca, Grossistas & Abastecedores.

5. **Teaser de Catálogo** (linhas 222–263): id `#catalogo`. Banner escuro com CTA para login.
   Imagem do catálogo com overlay de cadeado (acesso restrito).

6. **Testemunhos** (linhas 265–322): id `#testemunhos`. Três citações de clientes fictícios.

7. **Contactos** (linhas 324–393): id `#contactos`. Informações de contacto + mapa Google Maps
   embebido da Zona Industrial de Neiva, Viana do Castelo.

8. **Footer** (linhas 395–431): Logótipo, links de redes sociais, morada, direitos reservados.

**API endpoints usados:** Nenhum (página estática de marketing).

**Características especiais:** A página usa o mesmo sistema de tipografia e de cores
(Tailwind + daisyUI), mas **não** usa o layout partilhado `fragments/layout.html` — tem o
seu próprio `<head>` e estrutura. Isso é deliberado: a landing page tem um design diferente
da área autenticada (sem sidebar, sem navbar de utilizador).

---

### Página 2 — Login (/login)

**URL:** `GET /login` (formulário) e `POST /login` (submissão)
**Controller:** `src/main/java/com/example/webapp/controller/AuthController.java` (linhas 53–125)
**Template:** `src/main/resources/templates/login.html` (155 linhas)

**Conteúdo:** Layout split-screen em duas colunas:
- **Coluna esquerda** (painel de formulário, linhas 37–119): Logo, formulário de login com
  campos e-mail e password, checkbox "Lembrar dispositivo", mensagem de erro condicional,
  botão de submissão, link para abrir conta.
- **Coluna direita** (painel institucional, linhas 121–150): Fundo gradiente azul escuro,
  citação de marketing, imagem de produto com efeito de rotação CSS, rodapé técnico.

**API endpoints usados:** `POST /auth/login` (via `AuthService` → `IAuthApiService`).

**Características especiais:** A coluna direita tem `hidden lg:flex` — só aparece em ecrãs
grandes (≥ lg). Em mobile, apenas o formulário é visível.

---

### Página 3 — Dashboard / Área de Cliente (/client-area)

**URL:** `/client-area`
**Controller:** `src/main/java/com/example/webapp/controller/DashboardController.java` (97 linhas)
**Template:** `src/main/resources/templates/client-area.html` (76 linhas)

```java
// DashboardController.java — linhas 31–93
@GetMapping("/client-area")
public String clientArea(HttpSession session, Model model) {
    SessionUser user = sessionService.getUser(session);
    model.addAttribute("userName", user.getNome());  // linha 38

    try {
        PaginatedResponse<EncomendaResumoResponse> page =
                encomendaService.getByUserId(user.getId(), 0, 1000);  // linha 41: busca TUDO
        List<EncomendaResumoResponse> todasEncomendas = page.content;

        // Contagens por estado — linhas 45–53
        long totalPendentes = 0;
        long totalExpedidas = 0;
        long totalCanceladas = 0;
        for (EncomendaResumoResponse e : todasEncomendas) {
            if ("PENDENTE".equalsIgnoreCase(e.estado)) totalPendentes++;
            else if ("EXPEDIDA".equalsIgnoreCase(e.estado)) totalExpedidas++;
            else if ("CANCELADA".equalsIgnoreCase(e.estado)) totalCanceladas++;
        }

        // Produtos recentes (até 5, sem repetição) — linhas 59–81
        // Ordena encomendas por data DESC, extrai produtos únicos
        List<RecenteProdutoDTO> produtosRecentes = ...;

        model.addAttribute("produtosRecentes", produtosRecentes);
    } catch (Exception ex) {
        // fallback: tudo a zero
    }
}
```

O `DashboardController` define um `record` interno (Java 16+) para a lista de produtos recentes:

```java
// DashboardController.java — linha 95
public record RecenteProdutoDTO(String id, String nome) {}
```

**Conteúdo do template:**
- Saudação personalizada: `th:text="'Olá, ' + ${userName}"` (linha 9).
- Três cards de estatísticas daisyUI (linhas 15–42): Pendentes, Expedidas, Canceladas.
- Tabela "Produtos Recentes" (linhas 43–70): Lista dos últimos 5 produtos distintos encomendados,
  com link para `/produto/{id}`.

**API endpoints usados:** `GET /encomendas/user/{userId}?page=0&size=1000` (via `EncomendaService`).

---

### Página 4 — Catálogo de Produtos (/catalogo)

**URL:** `/catalogo?page={n}&size={m}`
**Controller:** `src/main/java/com/example/webapp/controller/CatalogoController.java` (78 linhas)
**Template:** `src/main/resources/templates/catalogo.html` (162 linhas)

**Conteúdo:**
- Cabeçalho com título e selector de tamanho de página.
- Tabela paginada de produtos (Nome/SKU, Estado Físico com badge colorido, Preço/kg, botão Detalhes).
- Estado vazio (nenhum produto disponível) com ícone e texto informativo.
- Barra de paginação (Anterior/Próxima) com indicador "Página X de Y".

**API endpoints usados:** `GET /produtos-finais/catalogo?page={n}&size={m}` (via `ProdutoCatalogoService`).

**Características especiais:** Uso de `EnumDisplayHelper` para converter `LIQUIDO`/`SOLIDO`
em "Líquido"/"Sólido" com badges de cores diferentes (`badge-info` para líquido, `badge-warning`
para sólido). O item de menu "Catálogo" na sidebar é realçado porque `activePage='catalogo'`.

---

### Página 5 — Detalhe de Produto (/produto/{id})

**URL:** `/produto/{id}`
**Controller:** `src/main/java/com/example/webapp/controller/ProdutoDetalheController.java` (187 linhas)
**Template:** `src/main/resources/templates/produto-detalhe.html` (206 linhas)

**Conteúdo:**
- Link de retorno ao catálogo (breadcrumb).
- Layout em duas colunas:
  - **Coluna esquerda (75%)**: Detalhe completo do produto (nome, SKU, estado físico,
    preço/kg, preço de venda, taxa IVA, validade, descrição).
  - **Coluna direita (25%, sticky)**: Painel "Adicionar ao Carrinho" com dropdown de
    tipos de pallet, input de quantidade, e indicador de itens no carrinho.

**API endpoints usados:**
- `GET /produtos-finais/{id}` — detalhe do produto
- `GET /pallet-tipos?page=0&size=200&sort=nome&direction=asc` — tipos de pallet para o dropdown

**Características especiais:** Formulário POST com PRG para `/produto/{id}`. JavaScript
mínimo para sincronizar o nome do pallet no hidden input.

---

### Página 6 — Carrinho de Compras (/carrinho)

**URL:** `/carrinho`
**Controller:** `src/main/java/com/example/webapp/controller/CarrinhoController.java` (111 linhas)
**Template:** `src/main/resources/templates/carrinho.html` (164 linhas)

**Conteúdo:**
- Estado vazio: ícone de carrinho, mensagem, botão "Voltar ao Catálogo".
- Estado com itens: tabela de itens (produto, pallet, quantidade, preço unitário, subtotal,
  botão de remoção), painel de resumo (subtotal, total, botão "Finalizar Encomenda").

**API endpoints usados:** Nenhum na visualização (apenas sessão); `POST /encomendas` no checkout.

**Características especiais:** Alertas flash (`th:if="${successMessage}"` e `th:if="${errorMessage}"`)
para feedback de remoção de items. Layout split-screen em mobile-first.

---

### Página 7 — Sucesso de Checkout (/checkout)

**URL:** `/checkout`
**Controller:** `src/main/java/com/example/webapp/controller/CarrinhoController.java` (linhas 101–104)
**Template:** `src/main/resources/templates/checkout.html` (30 linhas)

**Conteúdo:** Ícone verde de check, "Encomenda realizada com sucesso!", informação sobre
o processo de contacto comercial, botões para encomendas e catálogo.

**API endpoints usados:** Nenhum (página de resultado).

---

### Página 8 — Erro de Checkout (/checkout-error)

**URL:** `/checkout-error`
**Controller:** `src/main/java/com/example/webapp/controller/CarrinhoController.java` (linhas 106–109)
**Template:** `src/main/resources/templates/checkout-error.html` (30 linhas)

**Conteúdo:** Ícone vermelho de exclamação, mensagem de erro, botões para carrinho e catálogo.

**API endpoints usados:** Nenhum (página de resultado).

---

### Página 9 — Minhas Encomendas (/encomendas)

**URL:** `/encomendas?page={n}&size={m}`
**Controller:** `src/main/java/com/example/webapp/controller/MinhasEncomendasController.java` (80 linhas)
**Template:** `src/main/resources/templates/encomendas.html` (160 linhas)

**Conteúdo:**
- Selector de tamanho de página.
- Cards de estatísticas daisyUI (Pendentes, Expedidas, Canceladas).
- Tabela paginada de encomendas (data, estado com badge colorido, total em EUR, botão Detalhes).
- Barra de paginação.

**API endpoints usados:** `GET /encomendas/user/{userId}?page={n}&size={m}` (duas vezes:
uma com `size=1000` para estatísticas, outra com os parâmetros reais para a tabela).

---

### Página 10 — Detalhe de Encomenda (/encomendas/{id})

**URL:** `/encomendas/{id}`
**Controller:** `src/main/java/com/example/webapp/controller/EncomendaDetalheController.java` (83 linhas)
**Template:** `src/main/resources/templates/encomenda-detalhe.html` (124 linhas)

**Conteúdo:**
- Link de retorno às encomendas.
- Cabeçalho com referência (UUID), badge de estado, botão "Cancelar Encomenda" (só quando PENDENTE).
- Modal daisyUI de confirmação de cancelamento.
- Grelha de campos (data, total na moeda original, total EUR).
- Tabela de pallets encomendados (produto/SKU, tipo/capacidade, quantidade, preço/pallet, subtotal).

**API endpoints usados:**
- `GET /encomendas/{id}` — detalhe da encomenda
- `PATCH /encomendas/{id}/cancelar` — cancelamento (via POST do formulário)

**Características especiais:** Verificação de propriedade (`encomenda.userId == user.getId()`).
Modal daisyUI em vez de `confirm()` nativo.

---

### Página 11 — Sucesso de Cancelamento (/encomenda-cancelada-sucesso)

**URL:** `/encomenda-cancelada-sucesso`
**Controller:** `src/main/java/com/example/webapp/controller/EncomendaCanceladaController.java` (linhas 9–12)
**Template:** `src/main/resources/templates/encomenda-cancelada-sucesso.html` (27 linhas)

**Conteúdo:** Ícone verde de check, "Encomenda cancelada com sucesso", botão para lista
de encomendas.

---

### Página 12 — Erro de Cancelamento (/encomenda-cancelada-erro)

**URL:** `/encomenda-cancelada-erro`
**Controller:** `src/main/java/com/example/webapp/controller/EncomendaCanceladaController.java` (linhas 14–17)
**Template:** `src/main/resources/templates/encomenda-cancelada-erro.html` (30 linhas)

**Conteúdo:** Ícone vermelho de exclamação, mensagem de erro de cancelamento, botão
"Voltar à encomenda" (`javascript:history.back()`) e botão para lista de encomendas.

---

## Apêndice A — Interfaces Retrofit completas

### IAuthApiService

**Ficheiro:** `src/main/java/com/example/webapp/api/IAuthApiService.java` (33 linhas)

```java
public interface IAuthApiService {
    @POST("auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);  // linhas 30–31
}
```

### IProdutoCatalogoApiService

**Ficheiro:** `src/main/java/com/example/webapp/api/IProdutoCatalogoApiService.java` (56 linhas)

```java
public interface IProdutoCatalogoApiService {
    @GET("produtos-finais/catalogo")
    Call<PaginatedResponse<ProdutoCatalogoResponse>> findAllCatalogo(
            @Query("page") int page,
            @Query("size") int size
    );  // linhas 36–40

    @GET("produtos-finais/{id}")
    Call<ProdutoCatalogoResponse> findById(
            @Path("id") String id
    );  // linhas 51–54
}
```

### IPalletTipoApiService

**Ficheiro:** `src/main/java/com/example/webapp/api/IPalletTipoApiService.java` (44 linhas)

```java
public interface IPalletTipoApiService {
    @GET("pallet-tipos")
    Call<PaginatedResponse<PalletTipoResponse>> findAll(
            @Query("page")      int    page,
            @Query("size")      int    size,
            @Query("sort")      String sort,
            @Query("direction") String direction
    );  // linhas 36–42
}
```

### IEncomendaApiService

**Ficheiro:** `src/main/java/com/example/webapp/api/IEncomendaApiService.java` (36 linhas)

```java
public interface IEncomendaApiService {
    @POST("encomendas")
    Call<Object> criar(@Body CreateEncomendaRequest request);  // linhas 19–20

    @GET("encomendas/user/{userId}")
    Call<PaginatedResponse<EncomendaResumoResponse>> getByUserId(
            @Path("userId") UUID userId,
            @Query("page") int page,
            @Query("size") int size
    );  // linhas 22–27

    @GET("encomendas/{id}")
    Call<EncomendaDetalheResponse> getById(@Path("id") String id);  // linhas 29–30

    @PATCH("encomendas/{id}/cancelar")
    Call<ResponseBody> cancelar(@Path("id") String id);  // linhas 32–33
}
```

### IMoedaApiService

**Ficheiro:** `src/main/java/com/example/webapp/api/IMoedaApiService.java` (14 linhas)

```java
public interface IMoedaApiService {
    @GET("moedas/codigo/{codigo}")
    Call<MoedaResponse> getByCodigo(@Path("codigo") String codigo);  // linhas 10–11
}
```

---

## Apêndice B — Modelos de Resposta da API

### ProdutoCatalogoResponse

**Ficheiro:** `src/main/java/com/example/webapp/model/catalogo/ProdutoCatalogoResponse.java` (95 linhas)

| Campo | Tipo | Linha | Descrição |
|---|---|---|---|
| `id` | `String` | 19 | UUID do produto |
| `codigoSku` | `String` | 22 | Código SKU interno |
| `nome` | `String` | 25 | Nome comercial |
| `descricao` | `String` | 28 | Descrição |
| `abreviacaoSabor` | `String` | 31 | Ex.: "MOR", "BAN" |
| `estadoFisico` | `String` | 37 | "LIQUIDO" ou "SOLIDO" |
| `validadeDias` | `Integer` | 40 | Validade em dias |
| `precoVenda` | `BigDecimal` | 43 | Preço de venda unitário |
| `precoPorKg` | `BigDecimal` | 46 | Preço por quilograma |
| `taxaIva` | `BigDecimal` | 49 | Taxa IVA (ex.: 6.0 para 6%) |
| `visivelCliente` | `Boolean` | 52 | Visível no catálogo |
| `quantidadeLote` | `Integer` | 55 | Quantidade por lote |
| `composicao` | `List<ProdutoMateriaResponse>` | 58 | Composição de matérias-primas |
| `isActive` | `Boolean` | 61 | Produto activo |
| `createdAt` | `LocalDateTime` | 64 | Data de criação |
| `updatedAt` | `LocalDateTime` | 67 | Data de actualização |

### PalletTipoResponse

**Ficheiro:** `src/main/java/com/example/webapp/model/catalogo/PalletTipoResponse.java` (34 linhas)

| Campo | Tipo | Linha | Descrição |
|---|---|---|---|
| `id` | `String` | 17 | UUID do tipo de pallet |
| `nome` | `String` | 20 | Nome do tipo de pallet |
| `capacidadeKg` | `BigDecimal` | 23 | Capacidade máxima em kg |
| `isActive` | `Boolean` | 26 | Tipo de pallet activo |
| `createdAt` | `LocalDateTime` | 29 | Data de criação |
| `updatedAt` | `LocalDateTime` | 32 | Data de actualização |

### EncomendaResumoResponse e EncomendaDetalheResponse

**Ficheiros:** `model/encomenda/EncomendaResumoResponse.java` e `model/encomenda/EncomendaDetalheResponse.java`

Ambas têm exactamente os mesmos campos (linhas 6–20 em cada):

| Campo | Tipo | Descrição |
|---|---|---|
| `id` | `String` | UUID da encomenda |
| `userId` | `String` | UUID do utilizador |
| `userNome` | `String` | Nome do utilizador |
| `moedaId` | `String` | UUID da moeda |
| `moedaCodigo` | `String` | Código ISO da moeda (ex.: "EUR") |
| `moedaSimbolo` | `String` | Símbolo (ex.: "€") |
| `taxaConversaoSnapshot` | `Double` | Taxa de conversão no momento da encomenda |
| `estado` | `String` | "PENDENTE", "EXPEDIDA", "CANCELADA" |
| `dataEncomenda` | `String` | Data da encomenda (String, não LocalDate) |
| `totalPreco` | `Double` | Total na moeda original |
| `totalPrecoEur` | `Double` | Total em EUR |
| `pallets` | `List<EncomendaPalletResponse>` | Lista de pallets |
| `isActive` | `Boolean` | Encomenda activa |
| `createdAt` | `String` | Data de criação |
| `updatedAt` | `String` | Data de actualização |

### EncomendaPalletResponse

**Ficheiro:** `src/main/java/com/example/webapp/model/encomenda/EncomendaPalletResponse.java` (17 linhas)

| Campo | Tipo | Linha | Descrição |
|---|---|---|---|
| `id` | `String` | 4 | UUID da linha de pallet |
| `produtoId` | `String` | 5 | UUID do produto |
| `produtoNome` | `String` | 6 | Nome do produto |
| `produtoSku` | `String` | 7 | SKU do produto |
| `palletTipoId` | `String` | 8 | UUID do tipo de pallet |
| `palletTipoNome` | `String` | 9 | Nome do tipo de pallet |
| `palletCapacidadeKg` | `Double` | 10 | Capacidade em kg |
| `quantidadePallets` | `Integer` | 11 | Quantidade de pallets |
| `precoPorPalletEur` | `Double` | 12 | Preço por pallet em EUR (sem IVA) |
| `taxaIva` | `Double` | 13 | Taxa IVA (percentagem) |
| `subtotalEur` | `Double` | 14 | Subtotal sem IVA |
| `subtotalComIvaEur` | `Double` | 15 | Subtotal com IVA |

---

## Apêndice C — Configurações da Aplicação

### application.properties

**Ficheiro:** `src/main/resources/application.properties` (2 linhas)

```properties
spring.application.name=webapp
```

Apenas o nome da aplicação. Todas as configurações específicas do cliente HTTP estão
em `config.properties`.

### config.properties

**Ficheiro:** `src/main/resources/config.properties` (11 linhas)

```properties
# URL base da API backend (deve ter trailing slash)
api.base.url=http://localhost:8081/

# Timeout HTTP (segundos)
api.timeout.seconds=30

# Logging de pedidos/respostas HTTP na consola
api.logging.enabled=true
```

---

*Fim do documento ANALISE_WEB.md — gerado por leitura directa de todos os ficheiros fonte.*
