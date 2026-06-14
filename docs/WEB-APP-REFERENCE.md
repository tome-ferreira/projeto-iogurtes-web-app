# WEB-APP-REFERENCE.md — Guia de Referência da Web App

> Documentação técnica em Português Europeu para o portal B2B Norgurtes.

---

## Stack

| Camada | Tecnologia |
|--------|-----------|
| Framework | Spring Boot 4 (MVC + Thymeleaf) |
| HTTP Client | Retrofit 2.11 + OkHttp 4.12 (server-side) |
| JSON | Gson (com adaptadores LocalDate/LocalDateTime) |
| UI | Tailwind CSS (CDN) + daisyUI 4 + Font Awesome 6 |
| Tipografia | Plus Jakarta Sans (Google Fonts) |
| JavaScript | **Nenhum** — renderização 100% server-side |

---

## Estrutura de Pastas

```
web-app/src/main/
├── java/com/example/webapp/
│   ├── WebappApplication.java          ← Entry point Spring Boot
│   ├── api/
│   │   ├── ApiConfig.java              ← Lê config.properties (BASE_URL, TIMEOUT, LOGGING)
│   │   ├── ApiQuery.java               ← Executor síncrono: LOADING → SUCCESS | ERROR
│   │   ├── QueryState.java             ← State machine imutável
│   │   ├── RetrofitClient.java         ← Singleton OkHttp + Retrofit + Gson adapters
│   │   ├── IProdutoCatalogoApiService.java  ← GET /produtos-finais/catalogo + GET /produtos-finais/{id}
│   │   └── IPalletTipoApiService.java       ← GET /pallet-tipos
│   ├── config/
│   │   └── ApiConfig.java              ← Lê config.properties
│   ├── controller/
│   │   ├── WebController.java          ← Rotas genéricas (/, /login, /client-area, /encomendas)
│   │   ├── CatalogoController.java     ← GET /catalogo (com parâmetros page, size)
│   │   └── ProdutoDetalheController.java ← GET /produto/{id} + POST /produto/{id}/adicionar-carrinho
│   ├── model/
│   │   ├── PaginatedResponse.java      ← Wrapper genérico para respostas paginadas Spring
│   │   ├── SessionUser.java            ← Utilizador de sessão (mock → real)
│   │   ├── carrinho/
│   │   │   ├── CarrinhoItem.java       ← Uma linha do carrinho (produto + pallet + qty)
│   │   │   └── Carrinho.java           ← Carrinho guardado em sessão HTTP
│   │   └── catalogo/
│   │       ├── ProdutoCatalogoResponse.java  ← Modelo de /produtos-finais/catalogo e /produtos-finais/{id}
│   │       └── PalletTipoResponse.java       ← Modelo do endpoint /pallet-tipos
│   ├── service/
│   │   ├── ProdutoCatalogoService.java  ← getCatalogo() + getById()
│   │   ├── PalletTipoService.java       ← Encapsula chamadas HTTP aos tipos de pallet
│   │   ├── CarrinhoService.java         ← Gestão do carrinho em sessão HTTP
│   │   └── SessionService.java          ← Gestão do utilizador em sessão HTTP
│   └── util/
│       └── EnumDisplayHelper.java       ← @Component para converter enums em português
└── resources/
    ├── config.properties               ← BASE_URL, TIMEOUT, LOGGING
    ├── application.properties          ← Configuração Spring Boot
    ├── static/
    │   ├── css/
    │   │   └── dashboard.css           ← CSS customizado mínimo
    │   └── images/
    └── templates/
        ├── fragments/
        │   └── layout.html             ← Layout partilhado (sidebar, navbar, head)
        ├── index.html                  ← Página pública de landing
        ├── login.html                  ← Formulário de login
        ├── client-area.html            ← Dashboard B2B
        ├── catalogo.html               ← Catálogo paginado de produtos (link para detalhe)
        ├── produto-detalhe.html        ← Detalhe de produto + painel de carrinho
        └── encomendas.html             ← Histórico de encomendas
```

---

## Como Adicionar uma Nova Página

### 1. Criar modelo(s) em `model/{domínio}/`

```java
// model/catalogo/NovoDominioResponse.java
package com.example.webapp.model.catalogo;

public class NovoDominioResponse {
    public String id;
    public String nome;
    // campos exactamente como a API os devolve
}
```

### 2. Criar interface Retrofit em `api/`

```java
// api/INovoApiService.java
public interface INovoApiService {
    @GET("novo-endpoint")
    Call<PaginatedResponse<NovoDominioResponse>> findAll(
        @Query("page") int page,
        @Query("size") int size
    );
}
```

### 3. Criar serviço em `service/`

```java
// service/NovoService.java
@Service
public class NovoService {
    private final INovoApiService api =
        RetrofitClient.getInstance().getService(INovoApiService.class);

    public PaginatedResponse<NovoDominioResponse> getAll(int page, int size) {
        final PaginatedResponse<NovoDominioResponse>[] result = new PaginatedResponse[1];
        final String[] error = {null};

        ApiQuery.execute(api.findAll(page, size), state -> {
            if (state.isSuccess()) result[0] = state.getData();
            else if (state.isError()) error[0] = state.getErrorMessage();
        });

        if (error[0] != null) throw new RuntimeException(error[0]);
        return result[0];
    }
}
```

### 4. Criar controller em `controller/`

```java
// controller/NovoController.java
@Controller
public class NovoController {
    @Autowired private NovoService novoService;

    @GetMapping("/nova-pagina")
    public String novaPagina(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Model model
    ) {
        try {
            model.addAttribute("items", novoService.getAll(page, size));
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Erro ao carregar dados.");
            model.addAttribute("items", /* empty response */);
        }
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("pageSizeOptions", List.of(10, 20, 50));
        return "nova-pagina";
    }
}
```

### 5. Criar template em `templates/`

Copiar a estrutura do `catalogo.html` ou `encomendas.html` e adaptar.

---

## PaginatedResponse — Padrão

O Spring Boot devolve paginação com a seguinte estrutura JSON:

```json
{
  "content":          [...],
  "totalElements":    42,
  "totalPages":       5,
  "number":           0,
  "size":             10,
  "first":            true,
  "last":             false,
  "numberOfElements": 10,
  "empty":            false
}
```

> [!IMPORTANT]
> O campo de índice de página é **`number`** (não `currentPage` ou `page`).
> No controller, passa-se ao modelo como `currentPage` para legibilidade no template.

### No template Thymeleaf

```html
<!-- Número de resultados -->
<span th:text="${items.totalElements} + ' resultado(s)'"></span>

<!-- Paginação anterior -->
<a th:if="${!items.first}"
   th:href="@{/rota(page=${currentPage - 1}, size=${pageSize})}">Anterior</a>

<!-- Paginação próxima -->
<a th:if="${!items.last}"
   th:href="@{/rota(page=${currentPage + 1}, size=${pageSize})}">Próxima</a>

<!-- Indicador de página -->
<span th:text="'Página ' + (${currentPage} + 1) + ' de ' + ${items.totalPages}"></span>
```

---

## Enum Display — Padrão

### EnumDisplayHelper

O `EnumDisplayHelper` está registado como `@Component("enumDisplayHelper")`.
Thymeleaf expõe todos os beans Spring via `${@beanName}`.

```html
<!-- Usar no template: -->
<span th:text="${@enumDisplayHelper.getEstadoFisicoLabel(produto.estadoFisico)}"></span>
```

### Adicionar novo mapeamento

Em `util/EnumDisplayHelper.java`, adicionar um novo `Map` estático e um método getter:

```java
private static final Map<String, String> NOVO_ENUM = new HashMap<>();
static {
    NOVO_ENUM.put("VALOR_API", "Rótulo em Português");
}
public String getNovoEnumLabel(String valor) {
    if (valor == null) return "—";
    return NOVO_ENUM.getOrDefault(valor, valor);
}
```

### Mapeamentos existentes

| Enum | Valores da API | Rótulos PT |
|------|---------------|------------|
| `estadoFisico` | `LIQUIDO`, `SOLIDO` | Líquido, Sólido |
| `estadoEncomenda` | `PENDENTE`, `EXPEDIDA`, `CANCELADA` | Pendente, Expedida, Cancelada |
| `estadoEncomendaMp` | `PENDENTE`, `ENCOMENDADA`, `RECEBIDA`, `CANCELADA` | Pendente, Encomendada, Recebida, Cancelada |

---

## Paginação — Snippets de Template

### Selector de tamanho de página

```html
<form method="get" action="/rota">
    <select name="size" onchange="this.form.submit()">
        <option th:each="opt : ${pageSizeOptions}"
                th:value="${opt}"
                th:text="${opt}"
                th:selected="${opt == pageSize}">10</option>
    </select>
    <input type="hidden" name="page" value="0">
</form>
```

### Barra de paginação

```html
<div class="join">
    <a th:if="${!items.first}"
       th:href="@{/rota(page=${currentPage - 1}, size=${pageSize})}"
       class="join-item btn btn-sm">Anterior</a>
    <span th:if="${items.first}"
          class="join-item btn btn-sm btn-disabled">Anterior</span>

    <a th:if="${!items.last}"
       th:href="@{/rota(page=${currentPage + 1}, size=${pageSize})}"
       class="join-item btn btn-sm">Próxima</a>
    <span th:if="${items.last}"
          class="join-item btn btn-sm btn-disabled">Próxima</span>
</div>
```

---

## Tratamento de Erros — Padrão

### Nos serviços

O `ApiQuery.execute()` é síncrono. Em caso de erro HTTP ou de rede, o estado
transita para `ERROR` e o serviço lança uma excepção tipada.

```java
if (errorMessage[0] != null) {
    throw new MinhaServiceException(errorMessage[0]);
}
```

### Nos controllers

```java
try {
    model.addAttribute("items", service.getAll(page, size));
} catch (MinhaService.MinhaServiceException e) {
    log.warn("Erro: {}", e.getMessage());
    model.addAttribute("items", MinhaService.emptyResponse());
    model.addAttribute("errorMessage", "Não foi possível carregar os dados.");
}
```

### No template

```html
<div th:if="${errorMessage != null}" class="alert">
    <span th:text="${errorMessage}"></span>
</div>
```

---

## Exemplo de Referência: Página Catálogo

| Artefacto | Ficheiro |
|-----------|---------|
| Modelo | `model/catalogo/ProdutoCatalogoResponse.java` |
| Modelo (pallet) | `model/catalogo/PalletTipoResponse.java` |
| Paginação | `model/PaginatedResponse.java` |
| API interface (catálogo) | `api/IProdutoCatalogoApiService.java` |
| API interface (pallet) | `api/IPalletTipoApiService.java` |
| Serviço (catálogo) | `service/ProdutoCatalogoService.java` |
| Serviço (pallet) | `service/PalletTipoService.java` |
| Controller | `controller/CatalogoController.java` |
| Template | `templates/catalogo.html` |
| Enum helper | `util/EnumDisplayHelper.java` |

### Configuração da API

Editar apenas `src/main/resources/config.properties`:

```properties
api.base.url=http://localhost:8081/
api.timeout.seconds=30
api.logging.enabled=true
```

---

## Carrinho de Compras

### Modelos

| Classe | Ficheiro | Função |
|--------|----------|--------|
| `CarrinhoItem` | `model/carrinho/CarrinhoItem.java` | Uma linha do carrinho (produto + pallet + qty) |
| `Carrinho` | `model/carrinho/Carrinho.java` | Lista de itens; guardada na sessão HTTP |

### CarrinhoService — API

```java
@Autowired
private CarrinhoService carrinhoService;

// Ler carrinho (cria vazio se ausente)
Carrinho carrinho = carrinhoService.getCarrinho(session);

// Adicionar item
CarrinhoItem item = new CarrinhoItem(produtoId, produtoNome,
                                     palletTipoId, palletTipoNome,
                                     quantidadePallets);
carrinhoService.adicionarItem(session, item);

// Remover por índice (0-based)
carrinhoService.removerItem(session, 0);

// Limpar tudo
carrinhoService.limparCarrinho(session);

// Contar itens (para badge na navbar)
int total = carrinhoService.contarItens(session);
```

### Padrão de utilização num controller

```java
@Controller
public class MeuController {

    @Autowired private CarrinhoService carrinhoService;

    @GetMapping("/minha-pagina")
    public String pagina(HttpSession session, Model model) {
        model.addAttribute("carrinho", carrinhoService.getCarrinho(session));
        return "minha-pagina";
    }
}
```

### No template Thymeleaf

```html
<!-- Badge de itens no carrinho -->
<span th:text="${carrinho.totalItens()}">0</span>

<!-- Verificar se vazio -->
<div th:if="${!carrinho.isEmpty()}">
    <th:block th:each="item : ${carrinho.items}">
        <span th:text="${item.produtoNome}"></span>
        <span th:text="${item.quantidadePallets}"></span>
    </th:block>
</div>
```

### Chave de sessão

O `CarrinhoService` guarda o `Carrinho` sob a chave de sessão `"carrinho"`.
Não aceder directamente à sessão — usar sempre o serviço.

---

## Padrão de Página de Detalhe

### Quando usar página de detalhe vs modal

| Cenário | Recomendação |
|---------|-------------|
| Lista simples + consulta rápida de poucos campos | Modal (como era no catálogo) |
| Página com muitos campos, acções, formulários | **Página de detalhe dedicada** |
| PRG (Post-Redirect-Get) necessário | **Obrigatório página de detalhe** |

A partir desta versão, o catálogo usa páginas de detalhe (`/produto/{id}`) em vez de modais.

### Estrutura de uma página de detalhe

```
controller/ProdutoDetalheController.java   ← GET + POST
templates/produto-detalhe.html             ← Template com dois painéis
```

### PRG (Post-Redirect-Get)

Todos os formulários de acção (ex.: adicionar ao carrinho) seguem o padrão PRG:

```java
@PostMapping("/produto/{id}/adicionar-carrinho")
public String adicionarAoCarrinho(@PathVariable String id, ...) {
    // processar ...
    return "redirect:/produto/" + id;  // ← PRG: nunca return "view-name" após POST
}
```

**Porquê?** Evita resubmissão ao recarregar a página (F5 após um POST).

### Passar valores do servidor para JavaScript

> [!IMPORTANT]
> Nunca colocar expressões Thymeleaf dentro de strings JavaScript.
> Usar **data attributes** no HTML e ler com `dataset` em JS.

```html
<!-- Correcto: data attribute no HTML -->
<div id="cart-panel" th:data-preco="${produto.precoPorKg}">

<!-- Incorrecto: nunca fazer isto -->
<script>const preco = [[${produto.precoPorKg}]];</script>
```

```javascript
// Ler em JS via data attribute
const preco = parseFloat(document.getElementById('cart-panel').dataset.preco) || 0;
```
