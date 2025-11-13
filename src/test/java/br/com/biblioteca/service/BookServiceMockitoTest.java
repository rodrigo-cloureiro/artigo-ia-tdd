package br.com.biblioteca.service;

import br.com.biblioteca.model.Book;
import br.com.biblioteca.repository.InMemoryBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

public class BookServiceMockitoTest {

    private InMemoryBookRepository repo;
    private BookService service;

    @BeforeEach
    void setUp() {
        repo = mock(InMemoryBookRepository.class);
        service = new BookService(repo);
    }

    @Test
    void createDelegatesToRepoWhenValid() {
        Book book = new Book("T", "A", "1234567890123");
        when(repo.existsByIsbn("1234567890123")).thenReturn(false);
        when(repo.save(book)).thenReturn(book);
        service.create(book);
        verify(repo, times(1)).save(book);
    }
}
