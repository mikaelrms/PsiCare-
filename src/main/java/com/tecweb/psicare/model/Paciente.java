package com.tecweb.psicare.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "paciente")
@Getter
@Setter
@NoArgsConstructor
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome")
    private String nome;

    @NotBlank(message = "Informe o CPF")
    @Column(unique = true)
    private String cpf;

    private String telefone;

    @NotNull(message = "Informe a data de nascimento")
    @Past(message = "A data deve estar no passado")
    private LocalDate dataNascimento;

    // Obrigatório apenas para menores de 18 anos (validado no service/controller)
    private String responsavel;

    private boolean ativo = true;

    // Processamento automático: idade calculada (não vira coluna no banco)
    public int getIdade() {
        if (dataNascimento == null) return 0;
        return Period.between(dataNascimento, LocalDate.now()).getYears();
    }

    public boolean isMenor() {
        return getIdade() < 18;
    }
}