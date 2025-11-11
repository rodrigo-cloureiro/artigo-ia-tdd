package br.com.infnet.livraria_infnet.service;

import br.com.infnet.livraria_infnet.model.Livro;
import br.com.infnet.livraria_infnet.repository.LivroRepository;
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
public class LivroServiceTest {
    private Livro livro;
    @Mock
    private LivroRepository livroRepository;
    private LivroService livroService;

    @BeforeEach
    public void setUp() {
        livro = new Livro(
                "Refatoração - Aperfeiçoando o Projeto de Código Existente",
                "Martin Fowler",
                "https://m.media-amazon.com/images/I/81sTm5M7wjL._SY522_.jpg",
                "9788575227244",
                BigDecimal.valueOf(93.10),
                10,
                true);
        livroService = new LivroServiceImpl(livroRepository);
    }

    @AfterEach
    public void tearDown() {
        if (livro != null) {
            livro = null;
        }
        reset(livroRepository);
    }

    @Test
    public void deveAdicionarUmLivro() {
        when(livroRepository.add(livro)).thenReturn(livro);
        Livro adicionado = livroService.adicionar(livro);

        assertEquals(adicionado, livro);
        verify(livroRepository, times(1)).add(livro);
    }

    @Test
    public void deveRetornarListaDeLivrosAtivos() {
        when(livroRepository.findAll()).thenReturn(List.of(livro));
        List<Livro> livros = livroService.listar();

        assertEquals(1, livros.size());
        assertEquals("9788575227244", livros.getFirst().getIsbn());
    }

    @Test
    public void deveRetornarLivroPorIsbn() {
        when(livroRepository.findByIsbn("9788575227244")).thenReturn(Optional.ofNullable(livro));
        Optional<Livro> livroOpional = livroService.buscarPorIsbn("9788575227244");

        assertTrue(livroOpional.isPresent());
        assertEquals("Martin Fowler", livroOpional.get().getAutor());
    }
}
