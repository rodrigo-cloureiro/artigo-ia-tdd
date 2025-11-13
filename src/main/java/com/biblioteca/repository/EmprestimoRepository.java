package com.biblioteca.repository;

import com.biblioteca.model.Emprestimo;
import com.biblioteca.model.Livro;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class EmprestimoRepository {
    private final Map<Long, Emprestimo> emprestimosPorId = new ConcurrentHashMap<>();
    private final Map<Long, Emprestimo> emprestimosAtivosPorLivroId = new ConcurrentHashMap<>();

    public Emprestimo salvar(Emprestimo emprestimo) {
        if (emprestimo == null) {
            throw new IllegalArgumentException("Empréstimo não pode ser nulo");
        }

        // Verificar se o livro já está emprestado
        if (emprestimosAtivosPorLivroId.containsKey(emprestimo.getLivro().getId()) &&
                !emprestimo.isDevolvido()) {
            throw new IllegalArgumentException("Livro já está emprestado: " + emprestimo.getLivro().getTitulo());
        }

        emprestimosPorId.put(emprestimo.getId(), emprestimo);

        if (!emprestimo.isDevolvido()) {
            emprestimosAtivosPorLivroId.put(emprestimo.getLivro().getId(), emprestimo);
        } else {
            emprestimosAtivosPorLivroId.remove(emprestimo.getLivro().getId());
        }

        return emprestimo;
    }

    public Optional<Emprestimo> buscarPorId(Long id) {
        return Optional.ofNullable(emprestimosPorId.get(id));
    }

    public List<Emprestimo> buscarPorUsuario(String usuario) {
        if (usuario == null || usuario.trim().isEmpty()) {
            return List.of();
        }

        String usuarioLower = usuario.toLowerCase();
        return emprestimosPorId.values().stream()
                .filter(emprestimo -> emprestimo.getUsuario().toLowerCase().contains(usuarioLower))
                .collect(Collectors.toList());
    }

    public List<Emprestimo> buscarPorLivro(Livro livro) {
        if (livro == null) {
            return List.of();
        }

        return emprestimosPorId.values().stream()
                .filter(emprestimo -> emprestimo.getLivro().equals(livro))
                .collect(Collectors.toList());
    }

    public Optional<Emprestimo> buscarEmprestimoAtivoPorLivro(Livro livro) {
        if (livro == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(emprestimosAtivosPorLivroId.get(livro.getId()));
    }

    public boolean livroEstaEmprestado(Livro livro) {
        return livro != null && emprestimosAtivosPorLivroId.containsKey(livro.getId());
    }

    public List<Emprestimo> listarTodos() {
        return new ArrayList<>(emprestimosPorId.values());
    }

    public List<Emprestimo> listarAtivos() {
        return new ArrayList<>(emprestimosAtivosPorLivroId.values());
    }

    public List<Emprestimo> listarAtrasados() {
        return emprestimosPorId.values().stream()
                .filter(emprestimo -> !emprestimo.isDevolvido() && emprestimo.estaAtrasado())
                .collect(Collectors.toList());
    }

    public boolean deletar(Long id) {
        Optional<Emprestimo> emprestimo = buscarPorId(id);
        if (emprestimo.isPresent()) {
            Emprestimo emp = emprestimo.get();
            if (!emp.isDevolvido()) {
                throw new IllegalStateException("Não é possível excluir um empréstimo ativo");
            }

            emprestimosPorId.remove(id);
            emprestimosAtivosPorLivroId.remove(emp.getLivro().getId());
            return true;
        }
        return false;
    }

    public void limpar() {
        emprestimosPorId.clear();
        emprestimosAtivosPorLivroId.clear();
    }

    public int contar() {
        return emprestimosPorId.size();
    }

    public int contarAtivos() {
        return emprestimosAtivosPorLivroId.size();
    }
}