package com.br.infnet.repository.implementations;

import com.br.infnet.model.Emprestimo;
import com.br.infnet.model.Livro;
import com.br.infnet.repository.interfaces.iEmprestimoRepository;
import com.br.infnet.service.LivroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@DisplayName("Testes do EmprestimoRepositoryImpl")
class EmprestimoRepositoryImplTest {

    private iEmprestimoRepository emprestimoRepository;

    @Mock
    private LivroService livroService;

    private Livro livroTeste;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emprestimoRepository = new EmprestimoRepositoryImpl(livroService);

        livroTeste = new Livro(1, "Livro Teste", "Autor Teste", "9781234567890");
        livroTeste.setDisponivel(true);

        when(livroService.buscarLivroPorIDNoAcervo(anyInt())).thenReturn(livroTeste);
    }

    @Test
    @DisplayName("Deve realizar empréstimo com sucesso")
    void testRealizarEmprestimo() {
        LocalDate dataEmprestimo = LocalDate.now();
        LocalDate dataEstimada = dataEmprestimo.plusDays(7);
        Emprestimo emprestimo = new Emprestimo(0, 1, dataEmprestimo, dataEstimada, 7, 0.0);

        emprestimoRepository.realizarEmprestimo(emprestimo);

        List<Emprestimo> emprestimos = emprestimoRepository.listarEmprestimos();
        assertEquals(1, emprestimos.size());

        Emprestimo emprestimoSalvo = emprestimos.getFirst();
        assertEquals(1, emprestimoSalvo.getLivroId());
        assertEquals(dataEmprestimo, emprestimoSalvo.getDataEmprestimo());
        assertEquals(dataEstimada, emprestimoSalvo.getDataEstimadaDevolucao());

        verify(livroService).buscarLivroPorIDNoAcervo(1);
        assertFalse(livroTeste.isDisponivel());
    }

    @Test
    @DisplayName("Deve buscar empréstimo por ID do livro")
    void testBuscarLivroPorId() {
        LocalDate dataEmprestimo = LocalDate.now();
        Emprestimo emprestimo = new Emprestimo(0, 1, dataEmprestimo, dataEmprestimo.plusDays(7), 7, 0.0);
        emprestimoRepository.realizarEmprestimo(emprestimo);

        Emprestimo resultado = emprestimoRepository.buscarLivroPorId(1);

        assertNotNull(resultado);
        assertEquals(1, resultado.getLivroId());
        assertNull(resultado.getDataEfetivaDevolucao());
    }

    @Test
    @DisplayName("Deve retornar null ao buscar livro não emprestado")
    void testBuscarLivroPorIdNaoEmprestado() {
        Emprestimo resultado = emprestimoRepository.buscarLivroPorId(999);

        assertNull(resultado);
    }

    @Test
    @DisplayName("Deve listar apenas empréstimos ativos")
    void testListarEmprestimos() {
        LocalDate hoje = LocalDate.now();
        Emprestimo emprestimo1 = new Emprestimo(0, 1, hoje, hoje.plusDays(7), 7, 0.0);

        when(livroService.buscarLivroPorIDNoAcervo(2)).thenReturn(
                new Livro(2, "Livro 2", "Autor 2", "9781234567891"));

        Emprestimo emprestimo2 = new Emprestimo(0, 2, hoje, hoje.plusDays(14), 14, 0.0);

        emprestimoRepository.realizarEmprestimo(emprestimo1);
        emprestimoRepository.realizarEmprestimo(emprestimo2);

        List<Emprestimo> resultado = emprestimoRepository.listarEmprestimos();

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(emp -> emp.getDataEfetivaDevolucao() == null));
    }

    @Test
    @DisplayName("Deve remover empréstimo e atualizar livro")
    void testRemoverEmprestimo() {
        LocalDate dataEmprestimo = LocalDate.now();
        Emprestimo emprestimo = new Emprestimo(0, 1, dataEmprestimo, dataEmprestimo.plusDays(7), 7, 0.0);
        emprestimoRepository.realizarEmprestimo(emprestimo);

        List<Emprestimo> emprestimosAntes = emprestimoRepository.listarEmprestimos();
        assertEquals(1, emprestimosAntes.size());

        emprestimoRepository.removerEmprestimo(emprestimosAntes.getFirst());

        List<Emprestimo> emprestimosDepois = emprestimoRepository.listarEmprestimos();
        assertEquals(0, emprestimosDepois.size());

        verify(livroService, times(2)).buscarLivroPorIDNoAcervo(1);
        assertTrue(livroTeste.isDisponivel());
    }
}
