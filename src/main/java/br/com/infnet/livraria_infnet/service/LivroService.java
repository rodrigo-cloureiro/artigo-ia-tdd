package br.com.infnet.livraria_infnet.service;

import br.com.infnet.livraria_infnet.model.Livro;

import java.util.List;
import java.util.Optional;

public interface LivroService {
    List<Livro> listar();

    Optional<Livro> buscarPorIsbn(String isbn);
}
