package com.tecweb.psicare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "profissional")
@Getter
@Setter
@NoArgsConstructor
public class Profissional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                

    @NotBlank(message = "Informe o nome")
    private String nome;

    @NotBlank(message = "Informe o CRP ou matrícula")
    private String crp;

    @NotNull(message = "Informe o tipo")
    @Enumerated(EnumType.STRING)
    private TipoProfissional tipo;

    @NotNull(message = "Informe o valor da sessão")
    @Positive(message = "O valor deve ser maior que zero")
    @Column(precision = 10, scale = 2)
    private BigDecimal valorSessao;
}