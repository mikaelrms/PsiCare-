package com.tecweb.psicare.controller;

import com.tecweb.psicare.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("ind", dashboardService.indicadores());
        model.addAttribute("emRisco", dashboardService.pacientesEmRisco());

        // Separa nomes e valores em listas para o gráfico (Chart.js)
        List<String> nomes = new ArrayList<>();
        List<Long> quantidades = new ArrayList<>();
        for (Object[] linha : dashboardService.sessoesPorProfissional()) {
            nomes.add((String) linha[0]);
            quantidades.add((Long) linha[1]);
        }
        model.addAttribute("nomesProf", nomes);
        model.addAttribute("qtdProf", quantidades);

        model.addAttribute("repasse", dashboardService.faturamentoPorProfissional());
        return "dashboard";
    }
}