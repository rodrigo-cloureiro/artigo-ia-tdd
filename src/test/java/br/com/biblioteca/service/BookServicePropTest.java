package br.com.biblioteca.service;

import br.com.biblioteca.model.Book;
import br.com.biblioteca.repository.InMemoryBookRepository;
import net.jqwik.api.*;
import net.jqwik.api.constraints.NumericChars;
import net.jqwik.api.constraints.StringLength;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class BookServicePropTest {

    private BookService service;

    @BeforeEach
    void init() {
        service = new BookService(new InMemoryBookRepository());
    }

    @Property
    void isbnMustBeExactly13Digits(@ForAll @StringLength(min = 1, max = 20) String s) {
        // generate arbitrary strings and attempt to create; only numeric 13-digit allowed
        Book b = new Book("T", "A", s);
        if (s != null && s.matches("\\d{13}")) {
            // should create fine
            service.create(b);
        } else {
            assertThatThrownBy(() -> service.create(b)).hasMessageContaining("ISBN");
        }
    }
}
