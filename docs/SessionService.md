# Como usar nos controllers:

```java
@Controller
public class CatalogoController {

    @Autowired
    private SessionService sessionService;

    @GetMapping("/catalogo")
    public String catalogo(HttpSession session, Model model) {
        SessionUser user = sessionService.getUser(session);
        model.addAttribute("user", user);
        // ...
        return "catalogo";
    }
}
```

# Como usar no Thymeleaf:

```html
<span th:text="${user.nome}">Nome</span>
<span th:text="${user.role}">Role</span>
```

# Como migrar para login real:

## 1 passo:

No controller de login, após validar as credenciais contra a API:

```java
SessionUser user = new SessionUser(
    apiResponse.id,
    apiResponse.nome,
    apiResponse.email,
    apiResponse.role
);
sessionService.setUser(session, user);
```

## 2 passo:

 Em ``SessionService.getUser()``, substituir o mock:

```java
// Antes (mock):
return MOCK_USER;

// Depois (real):
SessionUser user = (SessionUser) session.getAttribute(SESSION_KEY);
if (user == null) throw new UnauthorizedException(); // ou redirect para login
return user;
```

## 3 passo:

Adicionar um interceptor Spring MVC que verifica se há utilizador na sessão em cada pedido protegido, redirecionando para /login se não existir.

## 4 passo:

Remover o ``MOCK_USER``.