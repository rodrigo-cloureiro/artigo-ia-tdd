package br.com.biblioteca.repository;

import br.com.biblioteca.model.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    Book save(Book book);
    Optional<Book> findById(Long id);
    Optional<Book> findByIsbn(String isbn);
    List<Book> findAll();
    List<Book> findByTitleContaining(String title);
    List<Book> findByAuthorContaining(String author);
    void deleteById(Long id);
    boolean existsByIsbn(String isbn);
    boolean existsByIsbnAndNotId(String isbn, Long id);
}
