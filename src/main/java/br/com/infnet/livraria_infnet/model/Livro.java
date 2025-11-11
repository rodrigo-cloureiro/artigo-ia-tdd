package br.com.infnet.livraria_infnet.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.math.BigDecimal;

@Getter
@ToString
public class Livro {
    private final String titulo;
    private final String autor;
    private final String capa;
    private final String isbn;
    private final BigDecimal preco;
    private final int quantidade;
    private final boolean ativo;

    @JsonCreator
    public Livro(
            @JsonProperty("titulo") String titulo,
            @JsonProperty("autor") String autor,
            @JsonProperty("capa") String capa,
            @JsonProperty("isbn") String isbn,
            @JsonProperty("preco") BigDecimal preco,
            @JsonProperty("quantidade") int quantidade,
            @JsonProperty("ativo") boolean ativo
    ) {
        this.titulo = titulo;
        this.autor = autor;
        this.capa = capa;
        this.isbn = isbn;
        this.preco = preco;
        this.quantidade = quantidade;
        this.ativo = ativo;
    }
}
