package br.com.infnet.livraria_infnet.controller;

import br.com.infnet.livraria_infnet.model.Livro;
import br.com.infnet.livraria_infnet.service.LivroService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
                "https://m.media-amazon.com/images/I/71dH97FwGbL._SY522_.jpg",
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
    public void deveAdicionarLivroComSucesso() {
        when(livroService.adicionar(livro)).thenReturn(livro);
        Livro adicionado = livroController.adicionar(livro);

        assertNotNull(adicionado);
        assertEquals(livro, adicionado);
        verify(livroService, times(1)).adicionar(livro);
    }

    @Test
    public void deveRetornarLivrosAtivosComSucesso() {
        when(livroService.listar()).thenReturn(List.of(livro));
        List<Livro> livros = livroController.listar();

        assertEquals(1, livros.size());
        verify(livroService, times(1)).listar();
    }

    @Test
    public void deveRetornarLivroPorIsbnComSucesso() {
        when(livroService.buscarPorIsbn("9788576082675")).thenReturn(Optional.ofNullable(livro));
        Optional<Livro> livroOptional = livroController.buscarPorIsbn("9788576082675");

        assertTrue(livroOptional.isPresent());
        assertTrue(livroOptional.get().getTitulo().contains("Código Limpo"));
        verify(livroService, times(1)).buscarPorIsbn("9788576082675");
    }

    @Test
    public void deveAtualizarLivroComSucesso() {
        when(livroService.atualizarLivro("9788576082675", livro)).thenReturn(livro);
        Livro livroAtualizado = livroController.atualizarLivro("9788576082675", livro);

        assertNotNull(livroAtualizado);
        assertEquals(livro, livroAtualizado);
        verify(livroService, times(1)).atualizarLivro("9788576082675", livro);
    }

    @Test
    public void deveRemoverLivroComSucesso() {
        doNothing().when(livroService).removerLivro(livro);
        livroController.removerLivro(livro);

        verify(livroService, times(1)).removerLivro(livro);
    }

    @Test
    public void deveRemoverLivroPorIsbnComSucesso() {
        doNothing().when(livroService).removerLivroPorIsbn("9788576082675");
        livroController.removerLivroPorIsbn("9788576082675");

        verify(livroService, times(1)).removerLivroPorIsbn("9788576082675");
    }
}
