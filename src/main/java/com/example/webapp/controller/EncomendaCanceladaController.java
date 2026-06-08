package com.example.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EncomendaCanceladaController {

    @GetMapping("/encomenda-cancelada-sucesso")
    public String sucesso() {
        return "encomenda-cancelada-sucesso";
    }

    @GetMapping("/encomenda-cancelada-erro")
    public String erro() {
        return "encomenda-cancelada-erro";
    }
}
