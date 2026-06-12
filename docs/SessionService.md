# SessionService — Guia de Referência

## Visão Geral

O `SessionService` gere o ciclo de vida do utilizador autenticado na sessão HTTP (`HttpSession`).
O utilizador é representado por `SessionUser`, que inclui o token JWT devolvido pela API após o login.

---

## SessionUser — Campos

| Campo   | Tipo     | Descrição                               |
|---------|----------|-----------------------------------------|
| `id`    | `UUID`   | Identificador único do utilizador       |
| `nome`  | `String` | Nome completo                           |
| `email` | `String` | Endereço de e-mail                      |
| `role`  | `String` | Papel do utilizador (ex.: `"CLIENTE"`)  |
| `token` | `String` | Token JWT para autenticação na API REST |

O campo `token` é definido via `user.setToken(resp.token)` após o login bem-sucedido.

---

## Métodos

| Método                                           | Descrição                                                        |
|--------------------------------------------------|------------------------------------------------------------------|
| `getUser(HttpSession session)`                   | Devolve o `SessionUser` da sessão, ou `null` se não autenticado |
| `setUser(HttpSession session, SessionUser user)` | Guarda o utilizador na sessão (incluindo token)                  |
| `clearUser(HttpSession session)`                 | Remove o utilizador (e token) da sessão                          |
| `isAuthenticated(HttpSession session)`           | `true` se existir utilizador na sessão                           |

---

## Ciclo de Vida — Login/Logout

```
Utilizador submete credenciais
         ↓
AuthController.login() chama AuthService.login(email, password)
         ↓
AuthService faz POST /auth/login à API REST (síncrono)
         ↓
LoginResponse { id, nome, email, role, token }
         ↓
SessionUser user = new SessionUser(id, nome, email, role)
user.setToken(token)
sessionService.setUser(session, user)
         ↓
redirect → /client-area (ou dashboard por role)
```

```
Utilizador clica "Terminar Sessão"
         ↓
POST /logout → AuthController.logout()
         ↓
sessionService.clearUser(session)
session.invalidate()
         ↓
redirect → /login
```

---

## Como Usar nos Controllers

```java
@Controller
public class DashboardController {

    @Autowired
    private SessionService sessionService;

    @GetMapping("/client-area")
    public String clientArea(HttpSession session, Model model) {
        SessionUser user = sessionService.getUser(session);
        if (user == null) {
            return "redirect:/login"; // protecção defensiva (o interceptor faz isto automaticamente)
        }
        model.addAttribute("userName", user.getNome());
        return "client-area";
    }
}
```

---

## Como Usar no Thymeleaf

```html
<span th:text="${user.nome}">Nome</span>
<span th:text="${user.role}">Role</span>
```

---

## Proteção de Rotas

A protecção é feita automaticamente pelo `AuthInterceptor` (registado no `WebMvcConfig`).
Não é necessário verificar manualmente a sessão em cada controller — apenas como salvaguarda defensiva.

Rotas **públicas** (excluídas do interceptor):
- `/` — landing page
- `/login` — formulário de login
- `/logout` — endpoint de logout
- `/css/**`, `/js/**`, `/images/**`, `/webjars/**` — recursos estáticos

---

> **Nota:** Não existe MOCK_USER. O `getUser()` devolve sempre o utilizador real da sessão, ou `null`.