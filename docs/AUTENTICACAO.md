# AUTENTICACAO.md — Autenticação na Web App

> Documentação técnica em Português Europeu para o portal B2B Norgurtes.

---

## Visão Geral

O fluxo completo de autenticação da web app:

```
1. Utilizador acede a rota protegida
          ↓
2. AuthInterceptor detecta sessão sem utilizador
          ↓
3. Redirect → GET /login
          ↓
4. Utilizador preenche email + password → POST /login
          ↓
5. AuthController chama AuthService.login()
          ↓
6. AuthService faz POST /auth/login à API REST (síncrono, Retrofit)
          ↓
7. API devolve { id, nome, email, role, token }
          ↓
8. SessionUser criado com token; guardado na HttpSession
          ↓
9. Redirect → /client-area (ou dashboard por role)
          ↓
10. OkHttp Interceptor lê token da sessão em cada pedido à API
          ↓
11. Header: Authorization: Bearer <token> em todos os pedidos
          ↓
12. POST /logout → session.invalidate() → redirect /login
```

---

## 1. Login

### Endpoint da API REST

```
POST /auth/login
Content-Type: application/json

{
  "email": "utilizador@empresa.com",
  "password": "palavra-passe"
}
```

**Resposta de sucesso (200 OK):**

```json
{
  "id":    "a29c851e-6104-4c16-89e8-bed0b70c7f64",
  "nome":  "Pedro Santos",
  "email": "pedro.santos@empresa.com",
  "role":  "CLIENTE",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Modelos

| Classe                                    | Função                              |
|-------------------------------------------|-------------------------------------|
| `model/auth/LoginRequest.java`            | Corpo do pedido (email, password)   |
| `model/auth/LoginResponse.java`           | Corpo da resposta (id, nome, email, role, token) |

### AuthController

**GET /login** — Apresenta o formulário. Se já autenticado, redirige para `/client-area`.

**POST /login** — Processa as credenciais:
- Sucesso → `SessionUser` criado com token → redirect por role
- Falha (4xx) → re-renderiza `login.html` com `${error}` e `${email}` pré-preenchido
- Erro de rede → re-renderiza com mensagem de erro de ligação

> **Nota PRG:** Em caso de erro de credenciais, o POST re-renderiza sem redirect (excepção
> documentada ao padrão PRG — aceitável para formulários de login, dado que
> é necessário mostrar o erro com o email pré-preenchido).

### AuthService

```java
// Chamada síncrona (.execute()) — adequado para Spring MVC server-side
public LoginResponse login(String email, String password) throws IOException {
    Response<LoginResponse> response =
            api.login(new LoginRequest(email, password)).execute();
    if (response.isSuccessful()) return response.body();
    return null; // 4xx — credenciais inválidas
}
```

---

## 2. SessionUser / SessionService

### SessionUser — Campos

| Campo   | Tipo     | Descrição                               |
|---------|----------|-----------------------------------------|
| `id`    | `UUID`   | Identificador único do utilizador       |
| `nome`  | `String` | Nome completo                           |
| `email` | `String` | Endereço de e-mail                      |
| `role`  | `String` | Papel do utilizador (ex.: `"CLIENTE"`)  |
| `token` | `String` | Token JWT para autenticação na API REST |

### SessionService — API

```java
// Criar e guardar utilizador após login
SessionUser user = new SessionUser(id, nome, email, role);
user.setToken(token);
sessionService.setUser(session, user);

// Ler utilizador (null se não autenticado)
SessionUser user = sessionService.getUser(session);

// Verificar autenticação
boolean autenticado = sessionService.isAuthenticated(session);

// Logout
sessionService.clearUser(session);
session.invalidate();
```

### Chave de sessão

O `SessionService` usa a chave `"loggedUser"` para guardar o `SessionUser` na `HttpSession`.

---

## 3. Proteção de Rotas

### Abordagem A — HandlerInterceptor (sem Spring Security)

A protecção é feita pelo `AuthInterceptor`, registado via `WebMvcConfig`.

**Sem Spring Security** — `spring-boot-starter-security` não está no `pom.xml`
(a validação JWT é feita pelo backend, não pela web app).

```java
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedUser") != null) {
            return true;
        }
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }
}
```

### Rotas públicas (excluídas do interceptor)

| Rota           | Motivo                  |
|----------------|-------------------------|
| `/`            | Landing page pública    |
| `/login`       | Formulário de login     |
| `/logout`      | Endpoint de logout      |
| `/css/**`      | Recursos estáticos CSS  |
| `/js/**`       | Recursos estáticos JS   |
| `/images/**`   | Imagens                 |
| `/webjars/**`  | Webjars                 |

Todas as outras rotas requerem autenticação. Se não autenticado, redirect para `/login`.

---

## 4. Envio do Token à API

### OkHttp Interceptor em RetrofitClient

O `RetrofitClient` é um singleton partilhado entre todos os pedidos HTTP.
O token **não pode** ser um campo estático — é lido por pedido via `RequestContextHolder`.

```java
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

### Limitação conhecida

> Se um pedido Retrofit for feito numa **thread de fundo** (sem `RequestContext` activo),
> o interceptor avança sem header `Authorization`. O backend rejeitará o pedido
> se o endpoint for protegido. Todos os pedidos actuais são síncronos na thread
> do pedido HTTP, pelo que esta limitação não afecta o comportamento normal.

---

## 5. Logout

```
POST /logout
         ↓
AuthController.logout()
         ↓
sessionService.clearUser(session)   — remove "loggedUser" da sessão
session.invalidate()                 — invalida a sessão HTTP inteira
         ↓
redirect → /login   (PRG — evita resubmissão)
```

O "Terminar Sessão" nos templates usa um formulário `<form method="POST" action="/logout">` —
não um link `<a href>` — para garantir o método HTTP correcto.

---

## 6. Diagrama de Fluxo

```
Browser                Web App (Spring MVC)           API REST (Backend)
   |                          |                               |
   |--- GET /client-area ---->|                               |
   |                    [AuthInterceptor: sem sessão]         |
   |<-- redirect /login ------|                               |
   |                          |                               |
   |--- GET /login ---------->|                               |
   |<-- 200 login.html -------|                               |
   |                          |                               |
   |--- POST /login --------->|                               |
   |  (email, password)  [AuthController.login()]             |
   |                          |--- POST /auth/login -------->|
   |                          |<-- { id, nome, email,        |
   |                          |      role, token }           |
   |                    [SessionUser criado com token]        |
   |                    [sessionService.setUser()]            |
   |<-- redirect /client-area-|                               |
   |                          |                               |
   |--- GET /catalogo ------->|                               |
   |                    [AuthInterceptor: sessão OK]          |
   |                          |--- GET /produtos-finais/catalogo --->|
   |                          |    Header: Authorization: Bearer <token>
   |                          |<-- 200 lista de produtos ----|
   |<-- 200 catalogo.html ----|                               |
   |                          |                               |
   |--- POST /logout -------->|                               |
   |                    [clearUser() + invalidate()]          |
   |<-- redirect /login ------|                               |
```

---

## Ficheiros Relevantes

| Ficheiro                                        | Função                                         |
|-------------------------------------------------|------------------------------------------------|
| `model/auth/LoginRequest.java`                  | Corpo do pedido de login                       |
| `model/auth/LoginResponse.java`                 | Corpo da resposta de login                     |
| `api/IAuthApiService.java`                      | Interface Retrofit para POST /auth/login       |
| `service/AuthService.java`                      | Lógica de chamada síncrona à API de login      |
| `model/SessionUser.java`                        | Utilizador de sessão (com token)               |
| `service/SessionService.java`                   | Gestão do utilizador na HttpSession            |
| `controller/AuthController.java`               | Rotas /login (GET, POST) e /logout (POST)      |
| `config/AuthInterceptor.java`                   | Interceptor HandlerInterceptor (Abordagem A)   |
| `config/WebMvcConfig.java`                      | Registo do interceptor e exclusões             |
| `api/RetrofitClient.java`                       | Singleton com interceptor JWT por pedido       |
| `templates/login.html`                          | Formulário de login com exibição de erros      |
