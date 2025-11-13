package br.com.biblioteca.service;

import br.com.biblioteca.model.Book;
import br.com.biblioteca.repository.InMemoryBookRepository;
import br.com.biblioteca.service.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

public class BookServiceTest {

    private BookService service;
    private InMemoryBookRepository repo;

    @BeforeEach
    public void setup() {
        repo = new InMemoryBookRepository();
        service = new BookService(repo);
    }

    @Test
    public void createShouldPersistValidBook() {
        Book b = new Book("Título X", "Autor Y", "1234567890123");
        Book saved = service.create(b);
        assertThat(saved.getId()).isNotNull();
        assertThat(service.findByIsbn("1234567890123")).isPresent();
    }

    @Test
    public void createShouldRejectDuplicateIsbn() {
        Book b1 = new Book("A", "B", "1111111111111");
        service.create(b1);
        Book b2 = new Book("C", "D", "1111111111111");
        assertThatThrownBy(() -> service.create(b2))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("ISBN já cadastrado");
    }

    @Test
    public void createShouldRejectInvalidIsbn() {
        Book b = new Book("T", "A", "abc");
        assertThatThrownBy(() -> service.create(b))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("ISBN deve conter exatamente 13 dígitos");
    }

    @Test
    public void updateShouldChangeFields() {
        Book b = new Book("Old", "Author", "2222222222222");
        Book saved = service.create(b);
        Book upd = new Book("New", "New Author", "2222222222222");
        Book updated = service.update(saved.getId(), upd);
        assertThat(updated.getTitle()).isEqualTo("New");
        assertThat(updated.getAuthor()).isEqualTo("New Author");
    }
}
