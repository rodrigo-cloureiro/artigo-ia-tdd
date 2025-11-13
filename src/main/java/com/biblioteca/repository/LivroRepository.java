package com.biblioteca.repository;

import com.biblioteca.model.Livro;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class LivroRepository {
    private final Map<Long, Livro> livrosPorId = new ConcurrentHashMap<>();
    private final Map<String, Livro> livrosPorIsbn = new ConcurrentHashMap<>();

    public Livro salvar(Livro livro) {
        if (livro == null) {
            throw new IllegalArgumentException("Livro não pode ser nulo");
        }

        // Verificar se ISBN já existe
        if (livrosPorIsbn.containsKey(livro.getIsbn())) {
            Livro existente = livrosPorIsbn.get(livro.getIsbn());
            if (!existente.getId().equals(livro.getId())) {
                throw new IllegalArgumentException("Já existe um livro com o ISBN: " + livro.getIsbn());
            }
        }

        livrosPorId.put(livro.getId(), livro);
        livrosPorIsbn.put(livro.getIsbn(), livro);
        return livro;
    }

    public Optional<Livro> buscarPorId(Long id) {
        return Optional.ofNullable(livrosPorId.get(id));
    }

    public Optional<Livro> buscarPorIsbn(String isbn) {
        return Optional.ofNullable(livrosPorIsbn.get(isbn));
    }

    public List<Livro> buscarPorTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return List.of();
        }

        String tituloLower = titulo.toLowerCase();
        return livrosPorId.values().stream()
                .filter(livro -> livro.getTitulo().toLowerCase().contains(tituloLower))
                .collect(Collectors.toList());
    }

    public List<Livro> buscarPorAutor(String autor) {
        if (autor == null || autor.trim().isEmpty()) {
            return List.of();
        }

        String autorLower = autor.toLowerCase();
        return livrosPorId.values().stream()
                .filter(livro -> livro.getAutor().toLowerCase().contains(autorLower))
                .collect(Collectors.toList());
    }

    public List<Livro> listarTodos() {
        return new ArrayList<>(livrosPorId.values());
    }

    public boolean deletar(Long id) {
        Optional<Livro> livro = buscarPorId(id);
        if (livro.isPresent()) {
            livrosPorId.remove(id);
            livrosPorIsbn.remove(livro.get().getIsbn());
            return true;
        }
        return false;
    }

    public boolean existePorIsbn(String isbn) {
        return livrosPorIsbn.containsKey(isbn);
    }

    public void limpar() {
        livrosPorId.clear();
        livrosPorIsbn.clear();
    }

    public int contar() {
        return livrosPorId.size();
    }
}