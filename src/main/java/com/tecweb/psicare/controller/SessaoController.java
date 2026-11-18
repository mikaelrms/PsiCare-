package com.tecweb.psicare.controller;

import com.tecweb.psicare.model.*;
import com.tecweb.psicare.repository.*;
import com.tecweb.psicare.service.SessaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/sessoes")
@RequiredArgsConstructor
public class SessaoController {

    private final SessaoService sessaoService;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalRepository profissionalRepository;
    private final SalaRepository salaRepository;

    // Alimentam os selects do formulário
    @ModelAttribute("pacientes")
    public List<Paciente> pacientes() {
        return pacienteRepository.findAll();
    }

    @ModelAttribute("profissionais")
    public List<Profissional> profissionais() {
        return profissionalRepository.findAll();
    }

    @ModelAttribute("salas")
    public List<Sala> salas() {
        return salaRepository.findAll();
    }

    @ModelAttribute("statusList")
    public StatusSessao[] statusList() {
        return StatusSessao.values();
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("sessoes", sessaoService.listar());
        return "sessoes/lista";
    }

    @GetMapping("/nova")
    public String nova(Model model) {
        model.addAttribute("sessao", new Sessao());
        return "sessoes/form";
    }

    @GetMapping("/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("sessao", sessaoService.buscar(id));
        return "sessoes/form";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute("sessao") Sessao sessao, BindingResult result) {

        // O select vazio chega como objeto com id nulo, então confere o id
        if (sessao.getPaciente() == null || sessao.getPaciente().getId() == null) {
            result.rejectValue("paciente", "paciente.obrigatorio", "Selecione o paciente");
        }
        if (sessao.getProfissional() == null || sessao.getProfissional().getId() == null) {
            result.rejectValue("profissional", "profissional.obrigatorio", "Selecione o profissional");
        }
        if (sessao.getSala() == null || sessao.getSala().getId() == null) {
            result.rejectValue("sala", "sala.obrigatoria", "Selecione a sala");
        }

        if (!result.hasErrors()) {
            sessaoService.preparar(sessao);

            if (sessaoService.conflitoProfissional(sessao)) {
                result.rejectValue("dataHora", "conflito.profissional",
                        "O profissional já tem sessão neste horário");
            }
            if (sessaoService.conflitoSala(sessao)) {
                result.rejectValue("dataHora", "conflito.sala",
                        "A sala já está ocupada neste horário");
            }
        }

        if (result.hasErrors()) {
            return "sessoes/form";
        }

        sessaoService.salvar(sessao);
        return "redirect:/sessoes";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id) {
        sessaoService.excluir(id);
        return "redirect:/sessoes";
    }
}