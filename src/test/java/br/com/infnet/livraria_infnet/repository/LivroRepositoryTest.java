package br.com.infnet.livraria_infnet.repository;

import br.com.infnet.livraria_infnet.model.Livro;
import org.junit.jupiter.api.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class LivroRepositoryTest {
    private LivroRepository livroRepository;

    @BeforeEach
    public void setUp() {
        livroRepository = new LivroRepositoryImpl();
    }

    @AfterEach
    public void tearDown() {
        if (livroRepository != null) {
            livroRepository = null;
        }
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
}
