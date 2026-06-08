package com.example.webapp.controller;

import com.example.webapp.model.PaginatedResponse;
import com.example.webapp.model.SessionUser;
import com.example.webapp.model.encomenda.EncomendaResumoResponse;
import com.example.webapp.service.EncomendaService;
import com.example.webapp.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class MinhasEncomendasController {

    private static final Logger log = LoggerFactory.getLogger(MinhasEncomendasController.class);

    private static final List<Integer> PAGE_SIZE_OPTIONS = List.of(10, 20, 50);

    @Autowired
    private EncomendaService encomendaService;

    @Autowired
    private SessionService sessionService;

    @GetMapping("/encomendas")
    public String minhasEncomendas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpSession session,
            Model model
    ) {
        SessionUser user = sessionService.getUser(session);

        // Calculate stats
        long totalPendentes = 0;
        long totalExpedidas = 0;
        long totalCanceladas = 0;

        try {
            PaginatedResponse<EncomendaResumoResponse> statsResponse = encomendaService.getByUserId(user.getId(), 0, 1000);
            if (statsResponse.content != null) {
                for (EncomendaResumoResponse enc : statsResponse.content) {
                    if ("PENDENTE".equals(enc.estado)) totalPendentes++;
                    else if ("EXPEDIDA".equals(enc.estado)) totalExpedidas++;
                    else if ("CANCELADA".equals(enc.estado)) totalCanceladas++;
                }
            }
        } catch (Exception e) {
            log.warn("Erro ao obter encomendas para estatísticas: {}", e.getMessage());
        }

        PaginatedResponse<EncomendaResumoResponse> encomendas;
        try {
            encomendas = encomendaService.getByUserId(user.getId(), page, size);
        } catch (Exception e) {
            log.error("Erro ao obter encomendas", e);
            encomendas = new PaginatedResponse<>();
            model.addAttribute("errorMessage", "Não foi possível carregar as encomendas.");
        }

        model.addAttribute("encomendas", encomendas);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("pageSizeOptions", PAGE_SIZE_OPTIONS);

        model.addAttribute("totalPendentes", totalPendentes);
        model.addAttribute("totalExpedidas", totalExpedidas);
        model.addAttribute("totalCanceladas", totalCanceladas);

        return "encomendas";
    }
}
