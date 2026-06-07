package com.example.webapp.controller;

import com.example.webapp.model.PaginatedResponse;
import com.example.webapp.model.catalogo.ProdutoCatalogoResponse;
import com.example.webapp.service.ProdutoCatalogoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Controller Spring MVC para a página de catálogo de produtos.
 *
 * <p>Renderiza o template {@code catalogo.html} com dados paginados de produtos
 * obtidos da API REST do backend.</p>
 *
 * <h3>Fluxo</h3>
 * <ol>
 *   <li>Recebe os parâmetros {@code page} e {@code size} do URL query string.</li>
 *   <li>Chama {@link ProdutoCatalogoService} para obter a página de produtos.</li>
 *   <li>Adiciona tudo ao {@link Model} e devolve o nome do template.</li>
 *   <li>Em caso de erro de API, adiciona resposta vazia e mensagem de erro ao modelo.</li>
 * </ol>
 *
 * <p>Os tipos de pallet são carregados no {@link ProdutoDetalheController},
 * não aqui — o catálogo apenas lista produtos e aponta para a página de detalhe.</p>
 */
@Controller
public class CatalogoController {

    private static final Logger log = LoggerFactory.getLogger(CatalogoController.class);

    /** Opções de tamanho de página disponíveis ao utilizador. */
    private static final List<Integer> PAGE_SIZE_OPTIONS = List.of(5, 10, 20, 50);

    @Autowired
    private ProdutoCatalogoService produtoCatalogoService;

    /**
     * Renderiza a página de catálogo com a lista paginada de produtos.
     *
     * @param page  índice da página corrente (0-based, default 0)
     * @param size  número de itens por página (default 10)
     * @param model modelo Thymeleaf
     * @return nome do template {@code "catalogo"}
     */
    @GetMapping("/catalogo")
    public String catalogo(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        // ── Buscar produtos do catálogo ───────────────────────────────────────
        PaginatedResponse<ProdutoCatalogoResponse> produtos;
        try {
            produtos = produtoCatalogoService.getCatalogo(page, size);
        } catch (ProdutoCatalogoService.ProdutoCatalogoException e) {
            log.warn("Falha ao carregar catálogo de produtos: {}", e.getMessage());
            produtos = ProdutoCatalogoService.emptyResponse();
            model.addAttribute("errorMessage",
                    "Não foi possível carregar o catálogo neste momento. Por favor, tente mais tarde.");
        }

        // ── Popular o modelo ──────────────────────────────────────────────────
        model.addAttribute("produtos",        produtos);
        model.addAttribute("currentPage",     page);
        model.addAttribute("pageSize",        size);
        model.addAttribute("pageSizeOptions", PAGE_SIZE_OPTIONS);

        return "catalogo";
    }
}
