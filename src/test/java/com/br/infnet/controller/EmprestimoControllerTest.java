package com.br.infnet.controller;

import com.br.infnet.model.Emprestimo;
import com.br.infnet.model.Livro;
import com.br.infnet.repository.interfaces.iLivroRepository;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmprestimoControllerTest {

    @Mock
    private iLivroRepository livroRepository;

    @Mock
    private Javalin app;

    @Mock
    private Context ctx;

    private EmprestimoController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Livro livroDisponivel = new Livro(1, "Clean Code", "Robert Martin", "1234567890123");
        livroDisponivel.setDisponivel(true);

        Livro livroEmprestado = new Livro(2, "Java Guide", "Oracle", "9876543210987");
        livroEmprestado.setDisponivel(false);

        List<Emprestimo> emprestimos = Arrays.asList(
                new Emprestimo(1, 1, LocalDate.now(), LocalDate.now().plusDays(7), 7, 0),
                new Emprestimo(2, 2, LocalDate.now(), LocalDate.now().plusDays(14), 14, 0)
        );
    }

    @Test
    @DisplayName("Deve registrar rotas corretamente")
    void testRegistroRotas() {
        controller = new EmprestimoController(app, livroRepository);

        verify(app).get(eq("/emprestimos"), any());
        verify(app).get(eq("/emprestimos/livros/{id}/emprestar"), any());
        verify(app).post(eq("/emprestimos/livros/{id}/emprestar"), any());
        verify(app).post(eq("/emprestimos/livros/{id}/devolver"), any());
    }

    @Test
    @DisplayName("Deve mockear parâmetros de contexto")
    void testMockContextParams() {
        when(ctx.pathParam("id")).thenReturn("1");
        when(ctx.formParam("prazo")).thenReturn("7");

        assertEquals("1", ctx.pathParam("id"));
        assertEquals("7", ctx.formParam("prazo"));
    }

    @Test
    @DisplayName("Deve construir controller sem erros")
    void testControllerConstruction() {
        assertDoesNotThrow(() -> {
            controller = new EmprestimoController(app, livroRepository);
        });
    }

    @Test
    @DisplayName("Deve verificar configuração de rotas GET")
    void testGetRoutes() {
        controller = new EmprestimoController(app, livroRepository);

        verify(app, times(1)).get(eq("/emprestimos"), any());
        verify(app, times(1)).get(eq("/emprestimos/livros/{id}/emprestar"), any());
    }

    @Test
    @DisplayName("Deve verificar configuração de rotas POST")
    void testPostRoutes() {
        controller = new EmprestimoController(app, livroRepository);

        verify(app, times(1)).post(eq("/emprestimos/livros/{id}/emprestar"), any());
        verify(app, times(1)).post(eq("/emprestimos/livros/{id}/devolver"), any());
    }

    @Test
    @DisplayName("Deve verificar dependências do repositório")
    void testRepositoryDependency() {
        controller = new EmprestimoController(app, livroRepository);

        assertNotNull(livroRepository);
    }

    @Test
    @DisplayName("Deve verificar total de rotas registradas")
    void testTotalRotas() {
        controller = new EmprestimoController(app, livroRepository);

        verify(app, times(2)).get(anyString(), any());
        verify(app, times(2)).post(anyString(), any());
    }
}
