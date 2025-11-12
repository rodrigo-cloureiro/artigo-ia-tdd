package br.com.infnet.livraria_infnet.controller;

import br.com.infnet.livraria_infnet.model.Livro;

import java.util.List;
import java.util.Optional;

public interface LivroController {
    Livro adicionar(Livro novoLivro);

    List<Livro> listar();

    Optional<Livro> buscarPorIsbn(String isbn);

    Livro atualizarLivro(String isbn, Livro livroAtualizado);

    void removerLivro(Livro livro);

    void removerLivroPorIsbn(String isbn);
}
