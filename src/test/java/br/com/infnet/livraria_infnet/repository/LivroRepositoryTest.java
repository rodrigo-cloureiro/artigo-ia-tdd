package br.com.infnet.livraria_infnet.repository;

import br.com.infnet.livraria_infnet.model.Livro;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
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
}
