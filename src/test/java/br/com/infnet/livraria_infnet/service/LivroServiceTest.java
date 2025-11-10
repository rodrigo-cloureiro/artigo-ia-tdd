package br.com.infnet.livraria_infnet.service;

import br.com.infnet.livraria_infnet.model.Livro;
import br.com.infnet.livraria_infnet.repository.LivroRepository;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class LivroServiceTest {
    private Livro livro;
    @Mock
    private LivroRepository livroRepository;
    @InjectMocks
    private LivroService livroService;

    @BeforeEach
    public void setUp() {
        livro = new Livro(
                "Refatoração - Aperfeiçoando o Projeto de Código Existente",
                "Martin Fowler",
                "9788575227244",
                BigDecimal.valueOf(93.10),
                10,
                true);
    }

    @AfterEach
    public void tearDown() {
        if (livro != null) {
            livro = null;
        }
        reset(livroRepository);
    }

    @Test
    public void deveRetornarListaDeLivrosAtivos() {
        when(livroRepository.findAll()).thenReturn(List.of(livro));
        List<Livro> livros = livroService.listar();

        assertEquals(1, livros.size());
        assertEquals("9788575227244", livros.getFirst().getIsbn());
    }
}
