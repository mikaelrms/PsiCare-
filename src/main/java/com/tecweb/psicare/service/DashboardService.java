package com.tecweb.psicare.service;

import com.tecweb.psicare.model.Paciente;
import com.tecweb.psicare.repository.PacienteRepository;
import com.tecweb.psicare.repository.SessaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SessaoRepository sessaoRepository;
    private final PacienteRepository pacienteRepository;

    public record Indicadores(
            long totalPacientes,
            long sessoesMes,
            long faltasMes,
            double taxaFaltas,
            BigDecimal faturamentoMes,
            BigDecimal emAberto
    ) {}

    public Indicadores indicadores() {
        LocalDateTime inicio = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime fim = inicio.plusMonths(1);

        long sessoes = sessaoRepository.contarNoPeriodo(inicio, fim);
        long faltas = sessaoRepository.contarFaltasNoPeriodo(inicio, fim);
        // Taxa de faltas = faltas / total de sessões do mês (em %)
        double taxa = sessoes == 0 ? 0 : (faltas * 100.0) / sessoes;

        return new Indicadores(
                pacienteRepository.count(),
                sessoes,
                faltas,
                taxa,
                sessaoRepository.faturamentoNoPeriodo(inicio, fim),
                sessaoRepository.totalEmAberto()
        );
    }

    public List<Object[]> sessoesPorProfissional() {
        return sessaoRepository.sessoesPorProfissional();
    }

    public List<Object[]> faturamentoPorProfissional() {
        LocalDateTime inicio = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        return sessaoRepository.faturamentoPorProfissional(inicio, inicio.plusMonths(1));
    }

    // Alerta: pacientes ativos sem sessão nos últimos 30 dias
    public List<Paciente> pacientesEmRisco() {
        return sessaoRepository.pacientesSemSessaoDesde(LocalDateTime.now().minusDays(30));
    }
}