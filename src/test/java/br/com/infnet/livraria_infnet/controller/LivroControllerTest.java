package br.com.infnet.livraria_infnet.controller;

import br.com.infnet.livraria_infnet.model.Livro;
import br.com.infnet.livraria_infnet.service.LivroService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LivroControllerTest {
    private Livro livro;
    @Mock
    private LivroService livroService;
    private LivroController livroController;

    @BeforeEach
    public void setUp() {
        livro = new Livro(
                "Código Limpo: Habilidades Práticas do Agile Software",
                "Robert C. Martin",
                "9788576082675",
                BigDecimal.valueOf(125.00),
                1,
                true
        );
        livroController = new LivroControllerImpl(livroService);
    }

    @AfterEach
    public void tearDown() {
        if (livroController != null) {
            livroController = null;
        }
        reset(livroService);
    }

    @Test
    public void deveRetornarLivrosAtivosComSucesso() {
        when(livroService.listar()).thenReturn(List.of(livro));
        List<Livro> livros = livroController.listar();
        assertEquals(1, livros.size());
    }
}
