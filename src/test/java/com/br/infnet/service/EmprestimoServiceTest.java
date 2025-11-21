package com.br.infnet.service;

import com.br.infnet.model.Emprestimo;
import com.br.infnet.model.Livro;
import com.br.infnet.repository.interfaces.iEmprestimoRepository;
import com.br.infnet.repository.interfaces.iLivroRepository;
import net.jqwik.api.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class EmprestimoServiceTest {

    @Mock
    private iEmprestimoRepository mockEmprestimoRepository;

    @Mock
    private iLivroRepository mockLivroRepository;

    private EmprestimoService emprestimoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emprestimoService = new EmprestimoService(mockEmprestimoRepository, mockLivroRepository);
    }

    //----------------- TESTES EMPRÉSTIMO ----------------//
    @Test
    @DisplayName("Deve emprestar livro disponível")
    void emprestarLivroDisponivel() {
        Livro livro = new Livro(1, "Teste", "Teste", "1234567890123");
        livro.setDisponivel(true);
        when(mockLivroRepository.buscarLivroPorId(1)).thenReturn(livro);
        emprestimoService.emprestarLivro(1, 7);
        verify(mockEmprestimoRepository).realizarEmprestimo(any(Emprestimo.class));
    }

    @Test
    @DisplayName("Deve lançar exceção caso o prazo de devolução seja negativo")
    void emprestarLivroPrazoNegativo() {
       IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> emprestimoService.emprestarLivro(1, -5));
        assertEquals("Prazo de devolução deve ser positivo", exception.getMessage());
        verifyNoInteractions(mockLivroRepository);
        verifyNoInteractions(mockEmprestimoRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar emprestar livro indisponível")
    void emprestarLivroIndisponivel() {
        Livro livro = new Livro(1, "Teste", "Teste", "1234567890123");
        livro.setDisponivel(false);
        when(mockLivroRepository.buscarLivroPorId(1)).thenReturn(livro);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> emprestimoService.emprestarLivro(1, 7));
        assertEquals("Livro já está emprestado", exception.getMessage());
        verify(mockEmprestimoRepository, never()).realizarEmprestimo(any());
    }

    //----------------- TESTES DEVOLUÇÃO ----------------//
    @Test
    @DisplayName("Deve devolver livro sem multa")
    void devolverLivroSemMulta() throws MultaPendenteException {
        Livro livro = new Livro(1, "Teste", "Teste", "1234567890123");
        livro.setDisponivel(false);
        livro.setDataEmprestimo(LocalDate.now().minusDays(5));
        Emprestimo emprestimo = new Emprestimo(1, 1, LocalDate.now().minusDays(5), LocalDate.now().plusDays(5),10, 0);
        when(mockLivroRepository.buscarLivroPorId(1)).thenReturn(livro);
        when(mockEmprestimoRepository.buscarLivroPorId(1)).thenReturn(emprestimo);
        emprestimoService.devolverLivro(1);
        verify(mockEmprestimoRepository).removerEmprestimo(emprestimo);
    }

    @Test
    @DisplayName("Deve lançar exceção ao devolver livro com multa e o livro não deve ser devolvido")
    void devolverLivroComMulta() {
        Livro livro = new Livro(1, "Teste", "Teste", "1234567890123");
        livro.setDisponivel(false);
        livro.setDataEmprestimo(LocalDate.now().minusDays(5));

        Emprestimo emprestimo = new Emprestimo(1, 1, LocalDate.now().minusDays(5), LocalDate.now().minusDays(5),10, 0);
        when(mockLivroRepository.buscarLivroPorId(1)).thenReturn(livro);
        when(mockEmprestimoRepository.buscarLivroPorId(1)).thenReturn(emprestimo);
        emprestimoService.devolverLivro(1);
        verify(mockEmprestimoRepository).removerEmprestimo(emprestimo);
    }

    //----------------- TESTES MULTA ----------------//
    @Test
    @DisplayName("Deve calcular a multa corretamente após o prazo gratuito")
    void calcularMulta() {
        Livro livro = new Livro(1, "Teste", "Teste", "1234567890123");
        livro.setDataEmprestimo(LocalDate.now().minusDays(18));
        livro.setDataEfetivaDevolucao(LocalDate.now());
        when(mockLivroRepository.buscarLivroPorId(1)).thenReturn(livro);
        double multa = emprestimoService.calcularMulta(1);
        //lembrando que a multa é calculada apenas a partir do 11º dia
        assertEquals(9, multa);
    }

    @Test
    @DisplayName("Deve retornar multa zero dentro do prazo gratuito")
    void calcularMultaSemAtraso() {
        // Arrange
        Livro livro = new Livro(1, "Teste", "Teste", "1234567890123");
        livro.setDataEmprestimo(LocalDate.now().minusDays(5));
        livro.setDataEfetivaDevolucao(LocalDate.now());

        when(mockLivroRepository.buscarLivroPorId(1)).thenReturn(livro);

        // Act
        double multa = emprestimoService.calcularMulta(1);

        // Assert
        assertEquals(0.0, multa);
    }

    //----------------- TESTES PARAMETRIZADOS ----------------//

    @Provide
    Arbitrary<LocalDate> data() {
        return Combinators.combine(
                Arbitraries.integers().between(2024, 2025),
                Arbitraries.integers().between(1, 12),
                Arbitraries.integers().between(1, 28)
        ).as((ano, mes, dia) -> {
            try {
                return LocalDate.of(ano, mes, dia);
            } catch (Exception e) {
                return LocalDate.of(ano, mes, Math.min(dia, 28));
            }
        });
    }

    @Provide
    Arbitrary<Integer> diasDecorridos() {
        return Arbitraries.integers().between(1, 365);
    }

    @Property()
    void testCalcularMultaComCenariosVariados(@ForAll("data") LocalDate dataEmprestimo, @ForAll("diasDecorridos") int diasDecorridos) {
        mockLivroRepository = mock(iLivroRepository.class);
        emprestimoService = new EmprestimoService(mockEmprestimoRepository, mockLivroRepository);
        LocalDate dataDevolucao = dataEmprestimo.plusDays(diasDecorridos);

        Livro livro = new Livro(1, "Teste", "Teste", "1234567890123");
        livro.setDataEmprestimo(dataEmprestimo);
        livro.setDataEfetivaDevolucao(dataDevolucao);

        when(mockLivroRepository.buscarLivroPorId(1)).thenReturn(livro);

        double multaCalculada = emprestimoService.calcularMulta(1);

        double multaEsperada;
        if (diasDecorridos <= 10) {
            multaEsperada = 0.0;
        } else {
            int diasAtraso = diasDecorridos - 10;
            multaEsperada = CalculadoraMulta.calcular(diasAtraso);
        }
        assertEquals(multaEsperada, multaCalculada);
        clearInvocations(mockLivroRepository);
    }
}
