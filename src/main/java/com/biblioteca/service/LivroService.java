package com.biblioteca.service;

import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import java.util.List;
import java.util.Optional;

public class LivroService {
    private final LivroRepository repository;

    public LivroService(LivroRepository repository) {
        this.repository = repository;
    }

    public Livro cadastrarLivro(Livro livro) {
        return repository.salvar(livro);
    }

    public Optional<Livro> buscarPorId(Long id) {
        return repository.buscarPorId(id);
    }

    public Optional<Livro> buscarPorIsbn(String isbn) {
        return repository.buscarPorIsbn(isbn);
    }

    public List<Livro> buscarPorTitulo(String titulo) {
        return repository.buscarPorTitulo(titulo);
    }

    public List<Livro> buscarPorAutor(String autor) {
        return repository.buscarPorAutor(autor);
    }

    public List<Livro> listarTodos() {
        return repository.listarTodos();
    }

    public Optional<Livro> atualizarLivro(Long id, Livro livroAtualizado) {
        Optional<Livro> livroExistente = repository.buscarPorId(id);
        if (livroExistente.isPresent()) {
            Livro livro = livroExistente.get();

            // Verificar se o ISBN foi alterado e se já existe
            if (!livro.getIsbn().equals(livroAtualizado.getIsbn()) &&
                    repository.existePorIsbn(livroAtualizado.getIsbn())) {
                throw new IllegalArgumentException("Já existe um livro com o ISBN: " + livroAtualizado.getIsbn());
            }

            livro.setTitulo(livroAtualizado.getTitulo());
            livro.setAutor(livroAtualizado.getAutor());
            livro.setIsbn(livroAtualizado.getIsbn());

            return Optional.of(repository.salvar(livro));
        }
        return Optional.empty();
    }

    public boolean deletarLivro(Long id) {
        return repository.deletar(id);
    }
}