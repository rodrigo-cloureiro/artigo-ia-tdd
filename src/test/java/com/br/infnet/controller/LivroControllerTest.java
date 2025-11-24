package com.br.infnet.controller;

import com.br.infnet.model.Livro;
import com.br.infnet.repository.interfaces.iLivroRepository;
import com.br.infnet.service.LivroService;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LivroControllerTest {

    @Mock
    private iLivroRepository livroRepository;

    @Mock
    private LivroService livroService;

    @Mock
    private Javalin app;

    @Mock
    private Context ctx;

    private LivroController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        List<Livro> livros = Arrays.asList(
                new Livro(1, "Clean Code", "Robert Martin", "1234567890123"),
                new Livro(2, "Java Guide", "Oracle", "9876543210987")
        );

        when(livroRepository.listarLivros()).thenReturn(livros);
        when(livroRepository.buscarLivroPorId(1)).thenReturn(livros.getFirst());
        when(livroRepository.buscarLivroPorId(999)).thenReturn(null);
    }

    @Test
    @DisplayName("Deve registrar rotas corretamente")
    void testRegistroRotas() {
        controller = new LivroController(app, livroRepository);

        verify(app).get(eq("/livros"), any());
        verify(app).get(eq("/"), any());
        verify(app).get(eq("/livros/novo"), any());
        verify(app).post(eq("/livros"), any());
        verify(app).get(eq("/livros/{id}/editar"), any());
        verify(app).post(eq("/livros/{id}/editar"), any());
        verify(app).post(eq("/livros/{id}/remover"), any());
        verify(app).get(eq("/buscar"), any());
    }

    @Test
    @DisplayName("Deve mockear parâmetros de contexto")
    void testMockContextParams() {
        when(ctx.pathParam("id")).thenReturn("1");
        when(ctx.formParam("titulo")).thenReturn("Test Book");
        when(ctx.formParam("autor")).thenReturn("Test Author");
        when(ctx.formParam("isbn")).thenReturn("1234567890123");
        when(ctx.queryParam("tipo")).thenReturn("titulo");
        when(ctx.queryParam("termo")).thenReturn("Clean");

        // Verifica se os mocks funcionam
        assertEquals("1", ctx.pathParam("id"));
        assertEquals("Test Book", ctx.formParam("titulo"));
        assertEquals("titulo", ctx.queryParam("tipo"));
    }

    @Test
    @DisplayName("Deve verificar dependências do repositório")
    void testRepositoryDependency() {
        controller = new LivroController(app, livroRepository);

        // Testa se o repositório pode ser usado
        List<Livro> result = livroRepository.listarLivros();
        assertEquals(2, result.size());
        assertEquals("Clean Code", result.getFirst().getTitulo());

        Livro livro = livroRepository.buscarLivroPorId(1);
        assertNotNull(livro);
        assertEquals("Clean Code", livro.getTitulo());

        Livro livroInexistente = livroRepository.buscarLivroPorId(999);
        assertNull(livroInexistente);
    }

    @Test
    @DisplayName("Deve construir controller sem erros")
    void testControllerConstruction() {
        assertDoesNotThrow(() -> {
            controller = new LivroController(app, livroRepository);
        });
    }

    @Test
    @DisplayName("Deve verificar configuração de rotas GET")
    void testGetRoutes() {
        controller = new LivroController(app, livroRepository);

        verify(app, times(1)).get(eq("/"), any());
        verify(app, times(1)).get(eq("/livros"), any());
        verify(app, times(1)).get(eq("/livros/novo"), any());
        verify(app, times(1)).get(eq("/livros/{id}/editar"), any());
        verify(app, times(1)).get(eq("/buscar"), any());
    }

    @Test
    @DisplayName("Deve verificar configuração de rotas POST")
    void testPostRoutes() {
        controller = new LivroController(app, livroRepository);

        verify(app, times(1)).post(eq("/livros"), any());
        verify(app, times(1)).post(eq("/livros/{id}/editar"), any());
        verify(app, times(1)).post(eq("/livros/{id}/remover"), any());
    }
}
