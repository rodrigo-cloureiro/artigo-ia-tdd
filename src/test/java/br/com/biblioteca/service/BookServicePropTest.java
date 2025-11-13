package br.com.biblioteca.service;

import br.com.biblioteca.model.Book;
import br.com.biblioteca.repository.JdbcBookRepository;
import net.jqwik.api.*;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BookServicePropTest {

    private BookService service;

    @BeforeEach
    void setup() { service = new BookService(new JdbcBookRepository()); }

    @Property
    void isbnMustBe13Digits(@ForAll @AlphaChars String s) {
        Book b = new Book("T", "A", s);
        if (s != null && s.matches("\\d{13}")) {
            service.create(b);
        } else {
            assertThatThrownBy(() -> service.create(b)).hasMessageContaining("ISBN");
        }
    }
}
