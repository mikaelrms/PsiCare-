package com.tecweb.psicare.repository;

import com.tecweb.psicare.model.Sessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface SessaoRepository extends JpaRepository<Sessao, Long> {

    List<Sessao> findAllByOrderByDataHoraDesc();

    List<Sessao> findByProfissionalIdAndDataHoraBetween(Long profissionalId, LocalDateTime inicio, LocalDateTime fim);

    List<Sessao> findBySalaIdAndDataHoraBetween(Long salaId, LocalDateTime inicio, LocalDateTime fim);

    // ---- Dashboard ----

    @Query("select count(s) from Sessao s where s.dataHora >= :inicio and s.dataHora < :fim")
    long contarNoPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("select count(s) from Sessao s where s.status = com.tecweb.psicare.model.StatusSessao.FALTA "
            + "and s.dataHora >= :inicio and s.dataHora < :fim")
    long contarFaltasNoPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    // Faturamento: soma dos valores das sessões realizadas no período
    @Query("select coalesce(sum(s.valor), 0) from Sessao s where s.status = com.tecweb.psicare.model.StatusSessao.REALIZADA "
            + "and s.dataHora >= :inicio and s.dataHora < :fim")
    BigDecimal faturamentoNoPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    // Valor ainda não pago (realizadas e não pagas)
    @Query("select coalesce(sum(s.valor), 0) from Sessao s where s.status = com.tecweb.psicare.model.StatusSessao.REALIZADA "
            + "and s.pago = false")
    BigDecimal totalEmAberto();

    // GROUP BY: sessões por profissional (nome, quantidade), da maior para a menor
    @Query("select s.profissional.nome, count(s) from Sessao s "
            + "where s.status <> com.tecweb.psicare.model.StatusSessao.CANCELADA "
            + "group by s.profissional.nome order by count(s) desc")
    List<Object[]> sessoesPorProfissional();

    // GROUP BY: faturamento por profissional (repasse)
    @Query("select s.profissional.nome, coalesce(sum(s.valor), 0) from Sessao s "
            + "where s.status = com.tecweb.psicare.model.StatusSessao.REALIZADA "
            + "and s.dataHora >= :inicio and s.dataHora < :fim "
            + "group by s.profissional.nome order by sum(s.valor) desc")
    List<Object[]> faturamentoPorProfissional(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    // Alerta de abandono: pacientes ativos sem nenhuma sessão (não cancelada) desde a data de corte
    @Query("select p from Paciente p where p.ativo = true and not exists ("
            + "select s.id from Sessao s where s.paciente = p "
            + "and s.status <> com.tecweb.psicare.model.StatusSessao.CANCELADA "
            + "and s.dataHora >= :corte)")
    List<com.tecweb.psicare.model.Paciente> pacientesSemSessaoDesde(@Param("corte") LocalDateTime corte);
}