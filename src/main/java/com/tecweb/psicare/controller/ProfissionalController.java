package com.tecweb.psicare.controller;

import com.tecweb.psicare.model.Profissional;
import com.tecweb.psicare.model.TipoProfissional;
import com.tecweb.psicare.repository.ProfissionalRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/profissionais")
@RequiredArgsConstructor
public class ProfissionalController {

    private final ProfissionalRepository profissionalRepository;

    //disponivel em todas as telas deste controller 
    @ModelAttribute("tipos")
    public TipoProfissional[] tipos() {
        return TipoProfissional.values();
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("profissionais", profissionalRepository.findAll());
        return "profissionais/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("profissional", new Profissional());
        return "profissionais/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("profissional", profissionalRepository.findById(id).orElseThrow());
        return "profissionais/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("profissional") Profissional profissional, BindingResult result) {
        if (result.hasErrors()) {
            return "profissionais/form";
        }
        profissionalRepository.save(profissional);
        return "redirect:/profissionais";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        profissionalRepository.deleteById(id);
        return "redirect:/profissionais";
    }
}