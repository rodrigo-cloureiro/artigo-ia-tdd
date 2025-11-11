package br.com.infnet.livraria_infnet.repository;

import br.com.infnet.livraria_infnet.model.Livro;

import java.util.List;
import java.util.Optional;

public interface LivroRepository {
    Livro add(Livro novoLivro);

    List<Livro> findAll();

    Optional<Livro> findByIsbn(String isbn);
}