package br.com.infnet.livraria_infnet.repository;

import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LivroRepositoryTests {
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
        assertEquals(26, livros.size());
    }
}
