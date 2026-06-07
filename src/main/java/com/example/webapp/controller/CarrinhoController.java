package com.example.webapp.controller;

import com.example.webapp.model.carrinho.Carrinho;
import com.example.webapp.service.CarrinhoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.webapp.model.SessionUser;
import com.example.webapp.model.carrinho.CarrinhoItem;
import com.example.webapp.model.encomenda.CreateEncomendaRequest;
import com.example.webapp.model.encomenda.EncomendaPalletItem;
import com.example.webapp.model.moeda.MoedaResponse;
import com.example.webapp.service.EncomendaService;
import com.example.webapp.service.MoedaService;
import com.example.webapp.service.SessionService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private MoedaService moedaService;

    @Autowired
    private EncomendaService encomendaService;

    @GetMapping("/carrinho")
    public String carrinho(HttpSession session, Model model) {
        Carrinho carrinho = carrinhoService.getCarrinho(session);
        model.addAttribute("carrinho", carrinho);
        return "carrinho";
    }

    @PostMapping("/carrinho/remover/{index}")
    public String removerItem(
            @PathVariable int index,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        try {
            carrinhoService.removerItem(session, index);
            redirectAttributes.addFlashAttribute("successMessage", "Item removido com sucesso.");
        } catch (IndexOutOfBoundsException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao remover item: índice inválido.");
        }
        return "redirect:/carrinho";
    }

    @PostMapping("/carrinho/finalizar")
    public String finalizar(HttpSession session) {
        SessionUser user = sessionService.getUser(session);
        if (user == null) {
            return "redirect:/login"; // ou error
        }

        Carrinho carrinho = carrinhoService.getCarrinho(session);
        if (carrinho == null || carrinho.isEmpty()) {
            return "redirect:/carrinho";
        }

        try {
            MoedaResponse moeda = moedaService.getByCodigo("EUR");
            
            List<EncomendaPalletItem> pallets = carrinho.getItems().stream()
                    .map(item -> new EncomendaPalletItem(
                            item.getProdutoId().toString(),
                            item.getPalletTipoId().toString(),
                            item.getQuantidadePallets()))
                    .collect(Collectors.toList());

            CreateEncomendaRequest request = new CreateEncomendaRequest(
                    user.getId().toString(),
                    moeda.id,
                    pallets
            );

            encomendaService.criar(request);

            carrinhoService.limparCarrinho(session);
            return "redirect:/checkout";

        } catch (Exception e) {
            return "redirect:/checkout-error";
        }
    }

    @GetMapping("/checkout")
    public String checkoutSuccess(Model model) {
        return "checkout";
    }

    @GetMapping("/checkout-error")
    public String checkoutError(Model model) {
        return "checkout-error";
    }
}
