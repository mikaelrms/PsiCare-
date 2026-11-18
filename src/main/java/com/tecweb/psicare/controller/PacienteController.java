package com.tecweb.psicare.controller;

import com.tecweb.psicare.model.Paciente;
import com.tecweb.psicare.repository.PacienteRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteRepository pacienteRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pacientes", pacienteRepository.findAll());
        return "pacientes/lista";
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("paciente", new Paciente());
        return "pacientes/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("paciente", pacienteRepository.findById(id).orElseThrow());
        return "pacientes/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("paciente") Paciente paciente, BindingResult result) {

        // Regra automática: menor de 18 anos exige responsável
        if (paciente.getDataNascimento() != null && paciente.isMenor()
                && (paciente.getResponsavel() == null || paciente.getResponsavel().isBlank())) {
            result.rejectValue("responsavel", "responsavel.obrigatorio",
                    "Paciente menor de 18 anos: informe o responsável");
        }

        // CPF duplicado
        if (paciente.getCpf() != null && !paciente.getCpf().isBlank()) {
            boolean duplicado = paciente.getId() == null
                    ? pacienteRepository.existsByCpf(paciente.getCpf())
                    : pacienteRepository.existsByCpfAndIdNot(paciente.getCpf(), paciente.getId());
            if (duplicado) {
                result.rejectValue("cpf", "cpf.duplicado", "Já existe um paciente com este CPF");
            }
        }

        if (result.hasErrors()) {
            return "pacientes/form";
        }
        pacienteRepository.save(paciente);
        return "redirect:/pacientes";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        pacienteRepository.deleteById(id);
        return "redirect:/pacientes";
    }
}