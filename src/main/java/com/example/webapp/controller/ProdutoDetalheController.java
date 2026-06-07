package com.example.webapp.controller;

import com.example.webapp.model.PaginatedResponse;
import com.example.webapp.model.carrinho.Carrinho;
import com.example.webapp.model.carrinho.CarrinhoItem;
import com.example.webapp.model.catalogo.PalletTipoResponse;
import com.example.webapp.model.catalogo.ProdutoCatalogoResponse;
import com.example.webapp.service.CarrinhoService;
import com.example.webapp.service.PalletTipoService;
import com.example.webapp.service.ProdutoCatalogoService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

/**
 * Controller Spring MVC para a página de detalhe de produto e gestão do carrinho.
 *
 * <h3>Rotas</h3>
 * <ul>
 *   <li>{@code GET  /produto/{id}} — mostra o detalhe completo de um produto</li>
 *   <li>{@code POST /produto/{id}/adicionar-carrinho} — adiciona ao carrinho (PRG)</li>
 * </ul>
 *
 * <h3>PRG (Post-Redirect-Get)</h3>
 * <p>O POST de adição ao carrinho redirige sempre para {@code GET /produto/{id}}
 * para evitar resubmissões ao recarregar a página.</p>
 */
@Controller
public class ProdutoDetalheController {

    private static final Logger log = LoggerFactory.getLogger(ProdutoDetalheController.class);

    @Autowired
    private ProdutoCatalogoService produtoCatalogoService;

    @Autowired
    private PalletTipoService palletTipoService;

    @Autowired
    private CarrinhoService carrinhoService;

    // ──────────────────────────────────────────────────────────────────────────
    // GET /produto/{id}
    // ──────────────────────────────────────────────────────────────────────────

    /**
     * Renderiza a página de detalhe de um produto final.
     *
     * <p>Carrega o produto via {@code GET /produtos-finais/{id}} e os tipos de
     * pallet disponíveis via {@code GET /pallet-tipos}. Em caso de erro, redirige
     * para o catálogo com uma mensagem de aviso.</p>
     *
     * @param id      UUID do produto (path variable)
     * @param session sessão HTTP corrente
     * @param model   modelo Thymeleaf
     * @return nome do template {@code "produto-detalhe"} ou redirect para {@code /catalogo}
     */
    @GetMapping("/produto/{id}")
    public String detalhe(
            @PathVariable String id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        // ── 1. Buscar produto por id ───────────────────────────────────────────
        ProdutoCatalogoResponse produto;
        try {
            produto = produtoCatalogoService.getById(id);
        } catch (ProdutoCatalogoService.ProdutoCatalogoException e) {
            log.warn("Produto não encontrado ou erro ao carregar [id={}]: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Produto não encontrado ou não disponível. Por favor, tente mais tarde.");
            return "redirect:/catalogo";
        }

        if (produto == null) {
            log.warn("Produto [id={}] devolveu resposta nula da API", id);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Produto não encontrado.");
            return "redirect:/catalogo";
        }

        // ── 2. Buscar tipos de pallet ──────────────────────────────────────────
        List<PalletTipoResponse> palletTipos;
        try {
            PaginatedResponse<PalletTipoResponse> paginatedPallets =
                    palletTipoService.getAll(0, 200, "nome", "asc");
            palletTipos = paginatedPallets.content != null
                    ? paginatedPallets.content
                    : List.of();
        } catch (PalletTipoService.PalletTipoException e) {
            log.warn("Falha ao carregar tipos de pallet para produto [id={}]: {}", id, e.getMessage());
            palletTipos = List.of();
        }

        // ── 3. Carrinho (para badge na navbar e eventual uso no template) ──────
        Carrinho carrinho = carrinhoService.getCarrinho(session);

        // ── 4. Popular modelo ─────────────────────────────────────────────────
        model.addAttribute("produto",      produto);
        model.addAttribute("palletTipos",  palletTipos);
        model.addAttribute("carrinho",     carrinho);

        return "produto-detalhe";
    }

    // ──────────────────────────────────────────────────────────────────────────
    // POST /produto/{id}/adicionar-carrinho
    // ──────────────────────────────────────────────────────────────────────────

    @PostMapping("/produto/{id}/adicionar-carrinho")
    public String adicionarAoCarrinho(
            @PathVariable String id,
            @RequestParam UUID palletTipoId,
            @RequestParam String palletTipoNome,
            @RequestParam int quantidadePallets,
            @RequestParam String produtoNome,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        // 1. Buscar produto para garantir que existe e obter o preço base (por kg)
        ProdutoCatalogoResponse produto;
        try {
            produto = produtoCatalogoService.getById(id);
        } catch (Exception e) {
            log.error("Erro ao adicionar carrinho: produto não encontrado", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao adicionar produto: não encontrado.");
            return "redirect:/catalogo";
        }

        // 2. Buscar capacidade da pallet
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
        } catch (Exception e) {
            log.warn("Falha ao obter capacidade da pallet, usará fallback de 1kg", e);
        }

        // 3. Calcular preço total da linha com IVA
        // Preço Unitário = (preço por Kg * capacidade da pallet) * (1 + taxaIVA/100)
        double precoBase = produto.precoPorKg != null ? produto.precoPorKg.doubleValue() : 0.0;
        double taxaIva = produto.taxaIva != null ? produto.taxaIva.doubleValue() : 0.0;
        
        double precoUnitarioBase = precoBase * capacidadePalletKg;
        double precoUnitario = precoUnitarioBase * (1.0 + (taxaIva / 100.0));
        double precoTotal = precoUnitario * quantidadePallets;

        CarrinhoItem item = new CarrinhoItem(
                UUID.fromString(id),
                produtoNome,
                palletTipoId,
                palletTipoNome,
                quantidadePallets,
                precoUnitario,
                precoTotal
        );
        carrinhoService.adicionarItem(session, item);
        log.info("Item adicionado ao carrinho: produto={}, pallet={}, qty={}, precoUnitario={}, precoTotal={}",
                produtoNome, palletTipoNome, quantidadePallets, precoUnitario, precoTotal);

        // PRG — redirige para GET para evitar resubmissão
        return "redirect:/produto/" + id;
    }
}
