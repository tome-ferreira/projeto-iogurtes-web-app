package com.example.webapp.controller;

import com.example.webapp.model.PaginatedResponse;
import com.example.webapp.model.SessionUser;
import com.example.webapp.model.encomenda.EncomendaPalletResponse;
import com.example.webapp.model.encomenda.EncomendaResumoResponse;
import com.example.webapp.service.EncomendaService;
import com.example.webapp.service.SessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Controller
public class DashboardController {

    private final SessionService sessionService;
    private final EncomendaService encomendaService;

    public DashboardController(SessionService sessionService, EncomendaService encomendaService) {
        this.sessionService = sessionService;
        this.encomendaService = encomendaService;
    }

    @GetMapping("/client-area")
    public String clientArea(HttpSession session, Model model) {
        SessionUser user = sessionService.getUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("userName", user.getNome());

        try {
            PaginatedResponse<EncomendaResumoResponse> page = encomendaService.getByUserId(user.getId(), 0, 1000);
            List<EncomendaResumoResponse> todasEncomendas = page.content;
            if (todasEncomendas == null) todasEncomendas = new ArrayList<>();

            long totalPendentes = 0;
            long totalExpedidas = 0;
            long totalCanceladas = 0;

            for (EncomendaResumoResponse e : todasEncomendas) {
                if ("PENDENTE".equalsIgnoreCase(e.estado)) totalPendentes++;
                else if ("EXPEDIDA".equalsIgnoreCase(e.estado)) totalExpedidas++;
                else if ("CANCELADA".equalsIgnoreCase(e.estado)) totalCanceladas++;
            }

            model.addAttribute("totalPendentes", totalPendentes);
            model.addAttribute("totalExpedidas", totalExpedidas);
            model.addAttribute("totalCanceladas", totalCanceladas);

            List<EncomendaResumoResponse> modifiableList = new ArrayList<>(todasEncomendas);
            modifiableList.sort(Comparator.comparing((EncomendaResumoResponse e) -> 
                e.dataEncomenda != null ? e.dataEncomenda : "").reversed());

            Set<String> seenProductIds = new LinkedHashSet<>();
            List<RecenteProdutoDTO> produtosRecentes = new ArrayList<>();

            for (EncomendaResumoResponse enc : modifiableList) {
                if (enc.pallets != null) {
                    for (EncomendaPalletResponse pallet : enc.pallets) {
                        if (!seenProductIds.contains(pallet.produtoId)) {
                            seenProductIds.add(pallet.produtoId);
                            produtosRecentes.add(new RecenteProdutoDTO(pallet.produtoId, pallet.produtoNome));
                            if (produtosRecentes.size() == 5) {
                                break;
                            }
                        }
                    }
                }
                if (produtosRecentes.size() == 5) {
                    break;
                }
            }

            model.addAttribute("produtosRecentes", produtosRecentes);

        } catch (Exception ex) {
            model.addAttribute("totalPendentes", 0);
            model.addAttribute("totalExpedidas", 0);
            model.addAttribute("totalCanceladas", 0);
            model.addAttribute("produtosRecentes", new ArrayList<>());
        }

        return "client-area";
    }

    public record RecenteProdutoDTO(String id, String nome) {}

}
