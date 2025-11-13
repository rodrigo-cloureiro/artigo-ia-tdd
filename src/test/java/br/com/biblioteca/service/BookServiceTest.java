package br.com.biblioteca.service;

import br.com.biblioteca.db.Database;
import br.com.biblioteca.model.Book;
import br.com.biblioteca.repository.JdbcBookRepository;
import org.junit.jupiter.api.*;

import static org.assertj.core.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookServiceTest {

    private BookService service;
    private Database db;

    @BeforeAll
    void initAll() {
        db = Database.getInstance();
    }

    @BeforeEach
    void setup() {
        service = new BookService(new JdbcBookRepository());
    }

    @Test
    void createAndFindByIsbn() {
        Book b = new Book("Título Test", "Autor Test", "0000000000001");
        Book saved = service.create(b);
        assertThat(saved.getId()).isNotNull();
        assertThat(service.findByIsbn("0000000000001")).isPresent();
    }

    @Test
    void rejectInvalidIsbn() {
        Book b = new Book("T", "A", "abc");
        assertThatThrownBy(() -> service.create(b)).isInstanceOf(Exception.class)
                .hasMessageContaining("ISBN");
    }

    @Test
    void updateChangesFields() {
        Book b = new Book("Old", "A", "0000000000002");
        Book s = service.create(b);
        Book upd = new Book("New", "B", "0000000000002");
        Book res = service.update(s.getId(), upd);
        assertThat(res.getTitle()).isEqualTo("New");
    }
}
