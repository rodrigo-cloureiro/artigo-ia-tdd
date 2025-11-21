package com.br.infnet.repository.interfaces;

import com.br.infnet.model.Emprestimo;

import java.util.List;

public interface iEmprestimoRepository {
    Emprestimo buscarLivroPorId(int livroId);
    void realizarEmprestimo(Emprestimo emprestimo);
    List<Emprestimo> listarEmprestimos();
    void removerEmprestimo(Emprestimo emprestimo);
}
