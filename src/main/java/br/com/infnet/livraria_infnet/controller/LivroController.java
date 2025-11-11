package br.com.infnet.livraria_infnet.controller;

import br.com.infnet.livraria_infnet.model.Livro;

import java.util.List;
import java.util.Optional;

public interface LivroController {
    List<Livro> listar();

    Optional<Livro> buscarPorIsbn(String isbn);
}
