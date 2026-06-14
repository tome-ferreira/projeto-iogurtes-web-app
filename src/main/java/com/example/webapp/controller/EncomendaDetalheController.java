package com.example.webapp.controller;

import com.example.webapp.model.SessionUser;
import com.example.webapp.model.encomenda.EncomendaDetalheResponse;
import com.example.webapp.service.EncomendaService;
import com.example.webapp.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EncomendaDetalheController {

    private static final Logger log = LoggerFactory.getLogger(EncomendaDetalheController.class);

    @Autowired
    private EncomendaService encomendaService;

    @Autowired
    private SessionService sessionService;

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
            log.warn("Encomenda não encontrada ou erro ao carregar [id={}]: {}", id, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Encomenda não encontrada.");
            return "redirect:/encomendas";
        }

        if (encomenda == null || !user.getId().toString().equals(encomenda.userId)) {
            log.warn("Acesso negado à encomenda [id={}] pelo user [userId={}]", id, user.getId());
            return "redirect:/encomendas";
        }

        model.addAttribute("encomenda", encomenda);

        return "encomenda-detalhe";
    }

    @PostMapping("/encomenda/{id}/cancelar")
    public String cancelar(@PathVariable String id, HttpSession session) {
        SessionUser user = sessionService.getUser(session);

        EncomendaDetalheResponse encomenda;
        try {
            encomenda = encomendaService.getById(id);
        } catch (Exception e) {
            log.warn("Encomenda não encontrada para cancelar [id={}]: {}", id, e.getMessage());
            return "redirect:/encomendas";
        }

        if (encomenda == null || !user.getId().toString().equals(encomenda.userId)) {
            log.warn("Acesso negado para cancelar encomenda [id={}] pelo user [userId={}]", id, user.getId());
            return "redirect:/encomendas";
        }

        try {
            encomendaService.cancelar(id);
            return "redirect:/encomenda-cancelada-sucesso";
        } catch (Exception e) {
            log.error("Erro ao cancelar a encomenda [id={}]", id, e);
            return "redirect:/encomenda-cancelada-erro";
        }
    }
}
