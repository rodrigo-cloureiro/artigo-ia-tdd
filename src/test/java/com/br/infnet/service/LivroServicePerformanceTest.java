package com.br.infnet.service;

import com.br.infnet.model.Livro;
import com.br.infnet.repository.interfaces.iLivroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testes de Performance e Timeout - LivroService")
public class LivroServicePerformanceTest {

    @Mock
    private iLivroRepository mockRepository;

    private LivroService livroService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        livroService = new LivroService(mockRepository);
    }

    @Test
    @Timeout(value = 2, unit = TimeUnit.SECONDS)
    @DisplayName("Listagem de livros deve responder em tempo hábil")
    void testTimeoutListarLivros() {
        List<Livro> livros = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            String isbn = String.format("978%010d", i);
            livros.add(new Livro(i, "Livro " + i, "Autor", isbn));
        }
        when(mockRepository.listarLivros()).thenReturn(livros);
        long inicio = System.currentTimeMillis();
        List<Livro> resultado = livroService.listarLivrosDoAcervo();
        long duracao = System.currentTimeMillis() - inicio;

        assertEquals(1000, resultado.size());
        assertTrue(duracao < 1000, "Operação muito lenta: " + duracao + "ms");
    }

    @Test
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    @DisplayName("Busca por ID deve ser rápida")
    void testTimeoutBuscarPorId() {
        Livro livro = new Livro(1, "Teste", "Teste", "1234567890123");
        when(mockRepository.buscarLivroPorId(1)).thenReturn(livro);

        assertDoesNotThrow(() -> {
            Livro encontrado = livroService.buscarLivroPorIDNoAcervo(1);
            assertNotNull(encontrado);
        });
    }

    @Test
    @DisplayName("Deve lidar com repositório indisponível graciosamente")
    void testRepositorioIndisponivel() {
        when(mockRepository.listarLivros()).thenThrow(new RuntimeException("Repositório indisponível"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> livroService.listarLivrosDoAcervo());
        assertEquals("Repositório indisponível", exception.getMessage());
    }
}
