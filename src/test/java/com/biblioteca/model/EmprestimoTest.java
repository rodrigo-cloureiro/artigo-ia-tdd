package com.biblioteca.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class EmprestimoTest {

    @Test
    @DisplayName("Deve criar empréstimo com dados válidos")
    void criarEmprestimoComDadosValidos() {
        Livro livro = new Livro("Dom Casmurro", "Machado de Assis", "9788535930001");
        Emprestimo emprestimo = new Emprestimo(livro, "João Silva", 10);

        assertEquals(livro, emprestimo.getLivro());
        assertEquals("João Silva", emprestimo.getUsuario());
        assertEquals(LocalDate.now(), emprestimo.getDataEmprestimo());
        assertEquals(LocalDate.now().plusDays(10), emprestimo.getDataDevolucaoPrevista());
        assertFalse(emprestimo.isDevolvido());
    }

    @Test
    @DisplayName("Deve calcular multa corretamente para atraso")
    void calcularMultaParaAtraso() {
        Livro livro = new Livro("Livro Teste", "Autor", "9788535930001");
        Emprestimo emprestimo = new Emprestimo(livro, "Usuário", 10);

        // Simular atraso de 5 dias
        emprestimo.setDataDevolucaoPrevista(LocalDate.now().minusDays(5));
        emprestimo.setDataDevolucaoReal(LocalDate.now());

        double multa = emprestimo.calcularMulta();
        assertEquals(5.0, multa); // Apenas os R$5,00 fixos
    }

    @Test
    @DisplayName("Deve calcular multa com dias extras")
    void calcularMultaComDiasExtras() {
        Livro livro = new Livro("Livro Teste", "Autor", "9788535930001");
        Emprestimo emprestimo = new Emprestimo(livro, "Usuário", 10);

        // Simular atraso de 15 dias (5 dias além do período fixo)
        emprestimo.setDataDevolucaoPrevista(LocalDate.now().minusDays(15));
        emprestimo.setDataDevolucaoReal(LocalDate.now());

        double multa = emprestimo.calcularMulta();
        assertEquals(5.0 + (5 * 0.5), multa); // R$5,00 + R$2,50
    }
}