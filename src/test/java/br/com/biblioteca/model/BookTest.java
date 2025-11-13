package br.com.biblioteca.model;

import net.jqwik.api.*;
import net.jqwik.api.constraints.AlphaChars;
import net.jqwik.api.constraints.NotBlank;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a entidade Book.
 * Embora 'record' seja simples, podemos testar os construtores se tivéssemos validação neles.
 * Como a validação está no Service, este teste é mais para Jqwik.
 * * Se a validação estivesse no construtor do Record (o que é uma boa prática),
 * os testes de falha seriam mais úteis aqui.
 */
class BookTest {

    @Property
    void bookRecordHoldsData(
            @ForAll("validIds") Long id,
            @ForAll @AlphaChars @NotBlank String title,
            @ForAll @AlphaChars @NotBlank String author,
            @ForAll("validIsbns") String isbn) {

        Book book = new Book(id, title, author, isbn);

        assertEquals(id, book.id());
        assertEquals(title, book.title());
        assertEquals(author, book.author());
        assertEquals(isbn, book.isbn());
    }

    @Property
    void bookAuxConstructor(
            @ForAll @AlphaChars @NotBlank String title,
            @ForAll @AlphaChars @NotBlank String author,
            @ForAll("validIsbns") String isbn) {

        Book book = new Book(title, author, isbn);

        assertNull(book.id()); // ID deve ser nulo no construtor auxiliar
        assertEquals(title, book.title());
        assertEquals(author, book.author());
        assertEquals(isbn, book.isbn());
    }

    // --- Geradores de Dados (Arbitraries) para Jqwik ---

    @Provide
    Arbitrary<Long> validIds() {
        return Arbitraries.longs().greaterOrEqual(1);
    }

    @Provide
    Arbitrary<String> validIsbns() {
        return Arbitraries.strings()
                .withCharRange('0', '9')
                .ofLength(13);
    }
}