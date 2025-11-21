package com.br.infnet.service;

import com.br.infnet.model.Emprestimo;
import com.br.infnet.model.Livro;
import com.br.infnet.repository.interfaces.iEmprestimoRepository;
import com.br.infnet.repository.interfaces.iLivroRepository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.NoSuchElementException;

public class EmprestimoService {
    private final iEmprestimoRepository emprestimoRepository;
    private final iLivroRepository livroRepository;

    public EmprestimoService(iEmprestimoRepository emprestimoRepository, iLivroRepository livroRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.livroRepository = livroRepository;
    }

    public Livro obterLivroPorId(int livroId) {
        return livroRepository.buscarLivroPorId(livroId);
    }

    public void emprestarLivro(int livroId, int prazoDevolucao) {
        validarPrazoEmprestimo(prazoDevolucao);

        Livro livro = livroRepository.buscarLivroPorId(livroId);
        if (livro == null) {
            throw new NoSuchElementException("Livro não encontrado");
        }

        if (!livro.isDisponivel()) {
            throw new IllegalStateException("Livro já está emprestado");
        }

        LocalDate dataEmprestimo = LocalDate.now();
        LocalDate dataEstimadaDevolucao = dataEmprestimo.plusDays(prazoDevolucao);

        Emprestimo emprestimo = new Emprestimo(0, livroId,dataEmprestimo, dataEstimadaDevolucao, prazoDevolucao, 0);
        emprestimoRepository.realizarEmprestimo(emprestimo);
    }

    public List<Emprestimo> listarEmprestimos() {
        return emprestimoRepository.listarEmprestimos();
    }

    public void devolverLivro(int livroId) throws MultaPendenteException {
        Livro livro = livroRepository.buscarLivroPorId(livroId);
        if (livro == null) {
            throw new NoSuchElementException("Livro não encontrado");
        }
        if (livro.isDisponivel()) {
            throw new IllegalStateException("Livro não está emprestado");
        }

        Emprestimo emprestimo = emprestimoRepository.buscarLivroPorId(livroId);
        if (emprestimo == null) {
            throw new NoSuchElementException("Empréstimo não encontrado para o livro");
        }

        double multa = calcularMulta(livroId);
        if (multa > 0) {
            livro.setMulta(multa);
            throw new MultaPendenteException("Pendente pagamento de multa no valor de R$ " + String.format("%.2f", multa));
        }
        emprestimoRepository.removerEmprestimo(emprestimo);
    }

    public double calcularMulta(int livroId) {
        Livro livro = obterLivroPorId(livroId);
        if (livro == null) {
            throw new NoSuchElementException("Livro não encontrado");
        }
        int diasAtraso = calcularDiasAtraso(livro);
        return CalculadoraMulta.calcular(diasAtraso);
    }

    private void validarPrazoEmprestimo(int prazo) {
        if (prazo <= 0) {
            throw new IllegalArgumentException("Prazo de devolução deve ser positivo");
        }
        if (prazo > 365) {
            throw new IllegalArgumentException("Prazo de devolução não pode exceder 365 dias");
        }
    }

    private int calcularDiasAtraso(Livro livro) {
        LocalDate dataEmprestimo = livro.getDataEmprestimo();
        LocalDate dataEfetivaDevolucao = livro.getDataEfetivaDevolucao();

        if (dataEfetivaDevolucao == null) {
            dataEfetivaDevolucao = LocalDate.now();
        }
        long diasDecorridos = ChronoUnit.DAYS.between(dataEmprestimo, dataEfetivaDevolucao);

        //Multa só se aplica se passou dos 10 dias gratuitos
        if (diasDecorridos <= 10) {
            return 0;
        }
        return Math.toIntExact(diasDecorridos - 10);
    }
}
