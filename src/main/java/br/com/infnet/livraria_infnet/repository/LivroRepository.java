package br.com.infnet.livraria_infnet.repository;

import br.com.infnet.livraria_infnet.model.Livro;

import java.util.List;

public interface LivroRepository {
    List<Livro> findAll();
}