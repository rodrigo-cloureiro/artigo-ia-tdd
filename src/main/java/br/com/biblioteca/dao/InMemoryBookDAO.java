package br.com.biblioteca.dao;

import br.com.biblioteca.model.Book;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementação em memória do BookDAO.
 * Utiliza um ConcurrentHashMap para garantir a segurança em ambientes com concorrência (thread-safety).
 */
public class InMemoryBookDAO implements BookDAO {

    private final Map<Long, Book> books = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Book save(Book book) {
        if (book.id() == null) {
            // Criar novo livro
            long newId = idGenerator.getAndIncrement();
            Book newBook = new Book(newId, book.title(), book.author(), book.isbn());
            books.put(newId, newBook);
            return newBook;
        } else {
            // Atualizar livro existente
            if (!books.containsKey(book.id())) {
                throw new IllegalArgumentException("Tentativa de atualizar livro inexistente com ID: " + book.id());
            }
            books.put(book.id(), book);
            return book;
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(books.get(id));
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return Optional.empty();
        }
        return books.values().stream()
                .filter(book -> isbn.equals(book.isbn()))
                .findFirst();
    }

    @Override
    public List<Book> findAll() {
        // Retorna uma lista ordenada por ID para consistência
        return books.values().stream()
                .sorted((b1, b2) -> b1.id().compareTo(b2.id()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Book> search(String searchTerm) {
        if (searchTerm == null || searchTerm.isBlank()) {
            return findAll();
        }

        final String lowerCaseTerm = searchTerm.toLowerCase();

        return books.values().stream()
                .filter(book -> book.title().toLowerCase().contains(lowerCaseTerm) ||
                        book.author().toLowerCase().contains(lowerCaseTerm) ||
                        book.isbn().contains(lowerCaseTerm)) // ISBN é exato ou parcial
                .sorted((b1, b2) -> b1.id().compareTo(b2.id()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteById(Long id) {
        return books.remove(id) != null;
    }
}