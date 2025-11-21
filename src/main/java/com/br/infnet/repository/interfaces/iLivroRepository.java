package com.br.infnet.repository.interfaces;
import com.br.infnet.model.Livro;
import java.util.List;

public interface iLivroRepository {
    int gerarProximoId();
    void salvarLivro(Livro livro);
    void atualizarLivro(Livro livro);
    void removerLivro(int id);
    Livro buscarLivroPorId(int id);
    Livro buscarLivroPorISBN(String isbn);
    List<Livro> listarLivros();
    List<Livro> listarLivrosPorTitulo(String titulo);
    List<Livro> listarLivrosPorAutor(String autor);
    boolean existeISBN(String isbn);

 }

