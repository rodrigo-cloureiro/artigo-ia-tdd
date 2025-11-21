package com.br.infnet.repository.implementations;

import com.br.infnet.model.Emprestimo;
import com.br.infnet.model.Livro;
import com.br.infnet.repository.interfaces.iEmprestimoRepository;
import com.br.infnet.service.LivroService;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EmprestimoRepositoryImpl implements iEmprestimoRepository {
    private final Map<Integer, Emprestimo> emprestimos = new HashMap<>();
    private final AtomicInteger proximoId = new AtomicInteger(1);
    private final LivroService livroService;

    public EmprestimoRepositoryImpl(LivroService livroService) {
        this.livroService = livroService;
    }

    @Override
    public Emprestimo buscarLivroPorId(int livroId) {
        return emprestimos.values().stream()
                .filter(emprestimo -> emprestimo.getLivroId() == livroId && emprestimo.getDataEfetivaDevolucao() == null)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void realizarEmprestimo(Emprestimo emprestimo) {
        if (emprestimo.getId() == 0) {
            emprestimo = new Emprestimo(proximoId.getAndIncrement(),
                    emprestimo.getLivroId(), emprestimo.getDataEmprestimo(),
                    emprestimo.getDataEstimadaDevolucao(), emprestimo.getPrazoDevolucao(),
                    emprestimo.getMulta());
        }
        emprestimos.put(emprestimo.getId(), emprestimo);
        atualizarDadosAposEmprestimo(emprestimo);
    }

    @Override
    public void removerEmprestimo(Emprestimo emprestimo) {
        emprestimos.remove(emprestimo.getId());
        atualizarDadosAposDevolucao(emprestimo.getLivroId());
    }

    @Override
    public List<Emprestimo> listarEmprestimos() {
        return emprestimos.values().stream()
                .filter(emprestimo -> emprestimo.getDataEfetivaDevolucao() == null)
                .toList();
    }

    private void atualizarDadosAposEmprestimo(Emprestimo emprestimo) {
        Livro livro = livroService.buscarLivroPorIDNoAcervo(emprestimo.getLivroId());
        livro.setDataEmprestimo(emprestimo.getDataEmprestimo());
        livro.setPrazoDevolucao(emprestimo.getPrazoDevolucao());
        livro.setDataEstimadaDevolucao(emprestimo.getDataEstimadaDevolucao());
        livro.setDisponivel(false);
    }

    private void atualizarDadosAposDevolucao(int livroId) {
        Livro livro = livroService.buscarLivroPorIDNoAcervo(livroId);
        livro.setDataEfetivaDevolucao(LocalDate.now());
        livro.setMulta(0);
        livro.setDisponivel(true);
        livro.setPrazoDevolucao(0);
        livro.setDataEmprestimo(null);
        livro.setDataEstimadaDevolucao(null);
        livro.setDataEfetivaDevolucao(null);
    }
}
