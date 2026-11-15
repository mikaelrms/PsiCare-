package com.tecweb.psicare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessao")
@Getter
@Setter
@NoArgsConstructor
public class Sessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Informe a data e hora")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dataHora;

    @Min(value = 15, message = "Duração mínima de 15 minutos")
    private int duracaoMin = 50;

    @Enumerated(EnumType.STRING)
    private StatusSessao status = StatusSessao.AGENDADA;

    // Preenchido automaticamente a partir do valor do profissional (no service)
    @Column(precision = 10, scale = 2)
    private BigDecimal valor;

    private boolean pago = false;

    @Column(length = 2000)
    private String observacao;

    @NotNull(message = "Selecione o paciente")
    @ManyToOne
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @NotNull(message = "Selecione o profissional")
    @ManyToOne
    @JoinColumn(name = "profissional_id", nullable = false)
    private Profissional profissional;

    @NotNull(message = "Selecione a sala")
    @ManyToOne
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    // Horário de término, útil para a verificação de conflito
    public LocalDateTime getDataHoraFim() {
        return dataHora == null ? null : dataHora.plusMinutes(duracaoMin);
    }
}