package com.biblioteca.service;

import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import com.biblioteca.validation.ValidationResult;
import java.util.List;
import java.util.Optional;

public class LivroService {
    private final LivroRepository repository;
    private final EmprestimoService emprestimoService;

    public LivroService(LivroRepository repository, EmprestimoService emprestimoService) {
        this.repository = repository;
        this.emprestimoService = emprestimoService;
    }

    public Livro cadastrarLivro(Livro livro) {
        // Validação do objeto
        ValidationResult validation = livro.validate();
        if (!validation.isValid()) {
            throw new IllegalArgumentException(validation.getErrorMessage());
        }

        return repository.salvar(livro);
    }

    public Optional<Livro> buscarPorId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }
        return repository.buscarPorId(id);
    }

    public Optional<Livro> buscarPorIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN não pode ser nulo ou vazio");
        }
        return repository.buscarPorIsbn(isbn);
    }

    public List<Livro> buscarPorTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return List.of();
        }
        return repository.buscarPorTitulo(titulo);
    }

    public List<Livro> buscarPorAutor(String autor) {
        if (autor == null || autor.trim().isEmpty()) {
            return List.of();
        }
        return repository.buscarPorAutor(autor);
    }

    public List<Livro> listarTodos() {
        return repository.listarTodos();
    }

    public Optional<Livro> atualizarLivro(Long id, Livro livroAtualizado) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        // Validação do objeto
        ValidationResult validation = livroAtualizado.validate();
        if (!validation.isValid()) {
            throw new IllegalArgumentException(validation.getErrorMessage());
        }

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
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("ID inválido");
        }

        Optional<Livro> livro = repository.buscarPorId(id);
        if (livro.isPresent()) {
            if (podeSerExcluido(livro.get())) {
                return repository.deletar(id);
            } else {
                throw new IllegalStateException("Não é possível excluir um livro que está emprestado");
            }
        }
        return false;
    }

    public boolean podeSerExcluido(Livro livro) {
        return !emprestimoService.livroEstaEmprestado(livro);
    }

    public boolean estaEmprestado(Livro livro) {
        return emprestimoService.livroEstaEmprestado(livro);
    }

    public Optional<Object> buscarEmprestimoAtivo(Livro livro) {
        return Optional.ofNullable(emprestimoService.buscarEmprestimoAtivoPorLivro(livro));
    }
}