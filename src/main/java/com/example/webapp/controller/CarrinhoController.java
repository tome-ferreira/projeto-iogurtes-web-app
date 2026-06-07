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

@Controller
public class CarrinhoController {

    @Autowired
    private CarrinhoService carrinhoService;

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
}
