package com.tecweb.psicare.controller;

import com.tecweb.psicare.model.Sala;
import com.tecweb.psicare.repository.SalaRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/salas")
@RequiredArgsConstructor
public class SalaController {

    private final SalaRepository salaRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("salas", salaRepository.findAll());
        return "salas/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("sala", new Sala());
        return "salas/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("sala", salaRepository.findById(id).orElseThrow());
        return "salas/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("sala") Sala sala, BindingResult result) {
        if (result.hasErrors()) {
            return "salas/form";
        }
        salaRepository.save(sala);
        return "redirect:/salas";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        salaRepository.deleteById(id);
        return "redirect:/salas";
    }
}