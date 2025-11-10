package br.com.infnet.livraria_infnet.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Getter
@ToString
public class Cliente {
    private final String nome;
    private final String email;
    private final String cpf;
    private final String numeroTelefone;
    private final Endereco endereco;
    private final Date dataNascimento;

    @JsonCreator
    public Cliente(
            @JsonProperty("nome") String nome,
            @JsonProperty("email") String email,
            @JsonProperty("cpf") String cpf,
            @JsonProperty("numeroTelefone") String numeroTelefone,
            @JsonProperty("endereco") Endereco endereco,
            @JsonProperty("dataNascimento") Date dataNascimento
    ) {
        this.nome = nome;
        this.email = email;
        this.cpf = cpf;
        this.numeroTelefone = numeroTelefone;
        this.endereco = endereco;
        this.dataNascimento = dataNascimento;
    }
}
