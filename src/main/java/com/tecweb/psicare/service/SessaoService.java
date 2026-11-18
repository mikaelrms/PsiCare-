package com.tecweb.psicare.service;

import com.tecweb.psicare.model.*;
import com.tecweb.psicare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessaoService {

    private final SessaoRepository sessaoRepository;
    private final PacienteRepository pacienteRepository;
    private final ProfissionalRepository profissionalRepository;
    private final SalaRepository salaRepository;

    // Troca os objetos "só com id" vindos do formulário pelas entidades completas
    // e preenche o valor automaticamente a partir do profissional.
    public void preparar(Sessao sessao) {
        sessao.setPaciente(pacienteRepository.findById(sessao.getPaciente().getId()).orElseThrow());
        sessao.setProfissional(profissionalRepository.findById(sessao.getProfissional().getId()).orElseThrow());
        sessao.setSala(salaRepository.findById(sessao.getSala().getId()).orElseThrow());

        if (sessao.getValor() == null) {
            sessao.setValor(sessao.getProfissional().getValorSessao());
        }
    }

    public boolean conflitoProfissional(Sessao nova) {
        LocalDateTime inicioDia = nova.getDataHora().toLocalDate().atStartOfDay();
        LocalDateTime fimDia = inicioDia.plusDays(1);
        List<Sessao> doDia = sessaoRepository.findByProfissionalIdAndDataHoraBetween(
                nova.getProfissional().getId(), inicioDia, fimDia);
        return doDia.stream().anyMatch(s -> sobrepoe(s, nova));
    }

    public boolean conflitoSala(Sessao nova) {
        LocalDateTime inicioDia = nova.getDataHora().toLocalDate().atStartOfDay();
        LocalDateTime fimDia = inicioDia.plusDays(1);
        List<Sessao> doDia = sessaoRepository.findBySalaIdAndDataHoraBetween(
                nova.getSala().getId(), inicioDia, fimDia);
        return doDia.stream().anyMatch(s -> sobrepoe(s, nova));
    }

    // Dois intervalos se sobrepõem quando um começa antes do outro terminar (e vice-versa).
    // Ignora a própria sessão (edição) e sessões canceladas.
    private boolean sobrepoe(Sessao existente, Sessao nova) {
        if (existente.getStatus() == StatusSessao.CANCELADA) return false;
        if (nova.getId() != null && nova.getId().equals(existente.getId())) return false;
        return existente.getDataHora().isBefore(nova.getDataHoraFim())
                && existente.getDataHoraFim().isAfter(nova.getDataHora());
    }

    public Sessao salvar(Sessao sessao) {
        return sessaoRepository.save(sessao);
    }

    public List<Sessao> listar() {
        return sessaoRepository.findAllByOrderByDataHoraDesc();
    }

    public Sessao buscar(Long id) {
        return sessaoRepository.findById(id).orElseThrow();
    }

    public void excluir(Long id) {
        sessaoRepository.deleteById(id);
    }
}