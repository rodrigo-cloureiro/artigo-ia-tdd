package com.biblioteca.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class LivroTest {

    @Test
    @DisplayName("Deve criar livro com dados válidos")
    void criarLivroComDadosValidos() {
        Livro livro = new Livro("Dom Casmurro", "Machado de Assis", "9788535930001");

        assertEquals("Dom Casmurro", livro.getTitulo());
        assertEquals("Machado de Assis", livro.getAutor());
        assertEquals("9788535930001", livro.getIsbn());
        assertNotNull(livro.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando título for nulo")
    void validarTituloNulo() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Livro(null, "Autor", "9788535930001"));
        assertEquals("título não pode ser nulo ou vazio", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando ISBN for inválido")
    void validarIsbnInvalido() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> new Livro("Título", "Autor", "123"));
        assertEquals("ISBN deve conter exatamente 13 dígitos numéricos", exception.getMessage());
    }

    @Test
    @DisplayName("Dois livros com mesmo ISBN devem ser iguais")
    void livrosComMesmoIsbnSaoIguais() {
        Livro livro1 = new Livro("Livro 1", "Autor 1", "9788535930001");
        Livro livro2 = new Livro("Livro 2", "Autor 2", "9788535930001");

        assertEquals(livro1, livro2);
        assertEquals(livro1.hashCode(), livro2.hashCode());
    }
}