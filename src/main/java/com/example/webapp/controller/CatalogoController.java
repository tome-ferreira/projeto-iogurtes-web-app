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

@Controller
public class CatalogoController {

    private static final Logger log = LoggerFactory.getLogger(CatalogoController.class);

    private static final List<Integer> PAGE_SIZE_OPTIONS = List.of(5, 10, 20, 50);

    @Autowired
    private ProdutoCatalogoService produtoCatalogoService;

    @GetMapping("/catalogo")
    public String catalogo(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        // Buscar produtos do catálogo
        PaginatedResponse<ProdutoCatalogoResponse> produtos;
        try {
            produtos = produtoCatalogoService.getCatalogo(page, size);
        } catch (ProdutoCatalogoService.ProdutoCatalogoException e) {
            log.warn("Falha ao carregar catálogo de produtos: {}", e.getMessage());
            produtos = ProdutoCatalogoService.emptyResponse();
            model.addAttribute("errorMessage",
                    "Não foi possível carregar o catálogo neste momento. Por favor, tente mais tarde.");
        }

        // Popular o modelo
        model.addAttribute("produtos", produtos);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("pageSizeOptions", PAGE_SIZE_OPTIONS);

        return "catalogo";
    }
}
