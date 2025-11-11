package br.com.infnet.livraria_infnet.repository;

import br.com.infnet.livraria_infnet.model.Livro;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class LivroRepositoryTest {
    private int quantidadeLivros;
    private Livro livro;
    private LivroRepository livroRepository;

    @BeforeEach
    public void setUp() {
        livroRepository = new LivroRepositoryImpl();
        quantidadeLivros = livroRepository.findAll().size();
        livro = new Livro(
                "O Programador Pragmático",
                "Andrew Hunt",
                "https://m.media-amazon.com/images/I/61hewOW+8zL._SY522_.jpg",
                "9788577807000",
                BigDecimal.valueOf(174.30),
                10,
                true
        );
    }

    @AfterEach
    public void tearDown() {
        if (livroRepository != null) {
            livroRepository = null;
        }
    }

    @Test
    public void deveAdicionarLivro() {
        livroRepository.add(livro);
        assertEquals(quantidadeLivros + 1, livroRepository.findAll().size());
    }

    @Test
    public void deveRetornarListaDeLivros() {
        List<Livro> livros = livroRepository.findAll();
        assertEquals(25, livros.size());
    }

    @Test
    public void deveRetornarLivroPorIsbn() {
        Optional<Livro> livroOptional = livroRepository.findByIsbn("9788594318619");
        assertTrue(livroOptional.isPresent());
        assertEquals("Memórias Póstumas de Brás Cubas", livroOptional.get().getTitulo());
    }

    @Test
    public void deveAtualizarLivro() {
        Optional<Livro> antesDeAtualizar = livroRepository.findByIsbn("9788594318619");
        assertTrue(antesDeAtualizar.isPresent());
        assertEquals("Memórias Póstumas de Brás Cubas", antesDeAtualizar.get().getTitulo());

        Livro livroAtualizado = livroRepository.update("9788594318619", livro);
        assertNotNull(livroAtualizado);
        assertEquals(livroAtualizado, livro);
        assertEquals("O Programador Pragmático", livroAtualizado.getTitulo());
    }

    @Test
    public void deveRemoverLivro() {
        Livro livro = livroRepository.findAll().getFirst();
        livroRepository.remove(livro);
        assertEquals(quantidadeLivros - 1, livroRepository.findAll().size());
    }

    @Test
    public void deveRemoverLivroPorIsbn() {
        livroRepository.removeByIsbn("9788594318619");
        assertEquals(quantidadeLivros - 1, livroRepository.findAll().size());
    }
}
