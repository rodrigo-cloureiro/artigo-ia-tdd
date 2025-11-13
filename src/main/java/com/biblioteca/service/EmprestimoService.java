package com.biblioteca.service;

import com.biblioteca.model.Emprestimo;
import com.biblioteca.model.Livro;
import com.biblioteca.repository.EmprestimoRepository;
import java.util.List;
import java.util.Optional;

public class EmprestimoService {
    private final EmprestimoRepository repository;
    private final LivroService livroService;

    public EmprestimoService(EmprestimoRepository repository, LivroService livroService) {
        this.repository = repository;
        this.livroService = livroService;
    }

    public Emprestimo realizarEmprestimo(Long livroId, String usuario, int prazoDias) {
        if (prazoDias > 10) {
            throw new IllegalArgumentException("Prazo máximo de empréstimo é 10 dias");
        }

        Optional<Livro> livro = livroService.buscarPorId(livroId);
        if (livro.isEmpty()) {
            throw new IllegalArgumentException("Livro não encontrado");
        }

        if (repository.livroEstaEmprestado(livro.get())) {
            throw new IllegalArgumentException("Livro já está emprestado: " + livro.get().getTitulo());
        }

        Emprestimo emprestimo = new Emprestimo(livro.get(), usuario, prazoDias);
        return repository.salvar(emprestimo);
    }

    public Emprestimo registrarDevolucao(Long emprestimoId) {
        Optional<Emprestimo> emprestimo = repository.buscarPorId(emprestimoId);
        if (emprestimo.isEmpty()) {
            throw new IllegalArgumentException("Empréstimo não encontrado");
        }

        Emprestimo emp = emprestimo.get();
        if (emp.isDevolvido()) {
            throw new IllegalArgumentException("Livro já foi devolvido");
        }

        emp.registrarDevolucao();
        return repository.salvar(emp);
    }

    public void pagarMulta(Long emprestimoId) {
        Optional<Emprestimo> emprestimo = repository.buscarPorId(emprestimoId);
        if (emprestimo.isEmpty()) {
            throw new IllegalArgumentException("Empréstimo não encontrado");
        }

        Emprestimo emp = emprestimo.get();
        emp.pagarMulta();
        repository.salvar(emp);
    }

    public Optional<Emprestimo> buscarPorId(Long id) {
        return repository.buscarPorId(id);
    }

    public List<Emprestimo> buscarPorUsuario(String usuario) {
        return repository.buscarPorUsuario(usuario);
    }

    public List<Emprestimo> buscarPorLivro(Livro livro) {
        return repository.buscarPorLivro(livro);
    }

    public Optional<Emprestimo> buscarEmprestimoAtivoPorLivro(Livro livro) {
        return repository.buscarEmprestimoAtivoPorLivro(livro);
    }

    public boolean livroEstaEmprestado(Livro livro) {
        return repository.livroEstaEmprestado(livro);
    }

    public List<Emprestimo> listarTodos() {
        return repository.listarTodos();
    }

    public List<Emprestimo> listarAtivos() {
        return repository.listarAtivos();
    }

    public List<Emprestimo> listarAtrasados() {
        return repository.listarAtrasados();
    }

    public boolean deletarEmprestimo(Long id) {
        return repository.deletar(id);
    }
}