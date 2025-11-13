package com.biblioteca.service;

import com.biblioteca.model.Livro;
import com.biblioteca.repository.LivroRepository;
import com.biblioteca.repository.EmprestimoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LivroServiceSecurityTest {

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private EmprestimoRepository emprestimoRepository;

    private LivroService livroService;
    private EmprestimoService emprestimoService;

    @BeforeEach
    void setUp() {
        emprestimoService = new EmprestimoService(emprestimoRepository, livroService);
        livroService = new LivroService(livroRepository, emprestimoService);
    }

    @Test
    @DisplayName("Deve rejeitar livro com título contendo XSS")
    void deveRejeitarLivroComXssNoTitulo() {
        Livro livro = new Livro("<script>alert('xss')</script>", "Autor Válido", "9788535930001");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> livroService.cadastrarLivro(livro));

        assertTrue(exception.getMessage().contains("Título inválido"));
    }

    @Test
    @DisplayName("Deve rejeitar livro com SQL Injection no autor")
    void deveRejeitarLivroComSqlInjectionNoAutor() {
        Livro livro = new Livro("Título Válido", "Autor'; DROP TABLE livros; --", "9788535930001");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> livroService.cadastrarLivro(livro));

        assertTrue(exception.getMessage().contains("Autor inválido"));
    }

    @Test
    @DisplayName("Deve rejeitar ISBN inválido")
    void deveRejeitarIsbnInvalido() {
        Livro livro = new Livro("Título Válido", "Autor Válido", "1234567890123");

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> livroService.cadastrarLivro(livro));

        assertTrue(exception.getMessage().contains("ISBN inválido"));
    }

    @Test
    @DisplayName("Deve aceitar livro com dados válidos")
    void deveAceitarLivroComDadosValidos() {
        Livro livro = new Livro("Dom Casmurro", "Machado de Assis", "9788535930001");
        when(livroRepository.salvar(any(Livro.class))).thenReturn(livro);

        Livro resultado = livroService.cadastrarLivro(livro);

        assertNotNull(resultado);
        verify(livroRepository).salvar(livro);
    }
}