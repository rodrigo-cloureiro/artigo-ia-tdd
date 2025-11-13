package br.com.biblioteca.service;

import br.com.biblioteca.model.Book;
import br.com.biblioteca.repository.InMemoryBookRepository;
import br.com.biblioteca.service.exceptions.BookNotFoundException;
import br.com.biblioteca.service.exceptions.ValidationException;

import java.util.List;
import java.util.Optional;

public class BookService {

    private final InMemoryBookRepository repository;

    public BookService(InMemoryBookRepository repository) {
        this.repository = repository;
    }

    public Book create(Book book) {
        validateBookForCreate(book);
        return repository.save(book);
    }

    public Book update(Long id, Book update) {
        validateBookForUpdate(id, update);
        Book existing = repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
        existing.setTitle(update.getTitle());
        existing.setAuthor(update.getAuthor());
        // if isbn changed, update index by saving new book (repository.save replaces index)
        existing.setIsbn(update.getIsbn());
        return repository.save(existing);
    }

    public void delete(Long id) {
        if (repository.findById(id).isEmpty()) throw new BookNotFoundException(id);
        repository.deleteById(id);
    }

    public Book findById(Long id) {
        return repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }

    public Optional<Book> findByIsbn(String isbn) {
        return repository.findByIsbn(isbn);
    }

    public List<Book> searchByTitle(String title) {
        return repository.findByTitleContainingIgnoreCase(title);
    }

    public List<Book> searchByAuthor(String author) {
        return repository.findByAuthorContainingIgnoreCase(author);
    }

    public List<Book> findAll() {
        return repository.findAll();
    }

    private void validateBookForCreate(Book book) {
        validateCommon(book);
        if (repository.existsByIsbn(book.getIsbn())) {
            throw new ValidationException("ISBN já cadastrado");
        }
    }

    private void validateBookForUpdate(Long id, Book book) {
        validateCommon(book);
        if (repository.existsByIsbnAndNotId(book.getIsbn(), id)) {
            throw new ValidationException("ISBN já cadastrado por outro livro");
        }
    }

    private void validateCommon(Book book) {
        if (book == null) throw new ValidationException("Livro inválido");
        if (book.getTitle() == null || book.getTitle().isBlank()) {
            throw new ValidationException("Título é obrigatório");
        }
        if (book.getAuthor() == null || book.getAuthor().isBlank()) {
            throw new ValidationException("Autor é obrigatório");
        }
        if (book.getIsbn() == null || book.getIsbn().isBlank()) {
            throw new ValidationException("ISBN é obrigatório");
        }
        String isbn = book.getIsbn();
        if (!isbn.matches("\\d{13}")) {
            throw new ValidationException("ISBN deve conter exatamente 13 dígitos numéricos");
        }
    }
}
