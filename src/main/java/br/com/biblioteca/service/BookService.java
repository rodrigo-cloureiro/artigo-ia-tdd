package br.com.biblioteca.service;

import br.com.biblioteca.model.Book;
import br.com.biblioteca.repository.BookRepository;
import br.com.biblioteca.repository.JdbcBookRepository;
import br.com.biblioteca.service.exceptions.BookNotFoundException;
import br.com.biblioteca.service.exceptions.ValidationException;
import br.com.biblioteca.util.InputSanitizer;
import br.com.biblioteca.util.InputValidator;

import java.util.List;
import java.util.Optional;

public class BookService {

    private final BookRepository repository;

    public BookService() {
        this(new JdbcBookRepository());
    }
    public BookService(BookRepository repository) { this.repository = repository; }

    public Book create(Book book) {
        sanitizeAndValidate(book);
        if (repository.existsByIsbn(book.getIsbn())) throw new ValidationException("ISBN já cadastrado");
        return repository.save(book);
    }

    public Book update(Long id, Book update) {
        if (id == null) throw new ValidationException("ID é obrigatório");
        sanitizeAndValidate(update);
        if (repository.existsByIsbnAndNotId(update.getIsbn(), id)) throw new ValidationException("ISBN já cadastrado por outro livro");
        Book existing = repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        existing.setTitle(update.getTitle());
        existing.setAuthor(update.getAuthor());
        existing.setIsbn(update.getIsbn());
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (id == null) throw new ValidationException("ID inválido");
        repository.deleteById(id);
    }

    public Book findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    public Optional<Book> findByIsbn(String isbn) { return repository.findByIsbn(isbn); }
    public List<Book> searchByTitle(String title) { return repository.findByTitleContaining(InputSanitizer.sanitize(title)); }
    public List<Book> searchByAuthor(String author) { return repository.findByAuthorContaining(InputSanitizer.sanitize(author)); }
    public List<Book> findAll() { return repository.findAll(); }

    private void sanitizeAndValidate(Book book) {
        if (book == null) throw new ValidationException("Livro inválido");
        book.setTitle(InputSanitizer.sanitize(book.getTitle()));
        book.setAuthor(InputSanitizer.sanitize(book.getAuthor()));
        book.setIsbn(InputSanitizer.sanitize(book.getIsbn()));
        try {
            InputValidator.requireNonEmpty(book.getTitle(), "Título");
            InputValidator.requireNonEmpty(book.getAuthor(), "Autor");
            InputValidator.validateIsbn(book.getIsbn());
        } catch (IllegalArgumentException e) {
            throw new ValidationException(e.getMessage());
        }
    }
}
