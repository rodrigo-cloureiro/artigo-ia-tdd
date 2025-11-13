package br.com.biblioteca.repository;

import br.com.biblioteca.model.Book;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class InMemoryBookRepository {

    private final Map<Long, Book> storage = new ConcurrentHashMap<>();
    private final Map<String, Long> isbnIndex = new ConcurrentHashMap<>();
    private final AtomicLong sequence = new AtomicLong(1);

    public synchronized Book save(Book book) {
        if (book.getId() == null) {
            long id = sequence.getAndIncrement();
            book.setId(id);
        }
        storage.put(book.getId(), book);
        isbnIndex.put(book.getIsbn(), book.getId());
        return book;
    }

    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Optional<Book> findByIsbn(String isbn) {
        Long id = isbnIndex.get(isbn);
        if (id == null) return Optional.empty();
        return findById(id);
    }

    public List<Book> findAll() {
        return new ArrayList<>(storage.values())
                .stream()
                .sorted(Comparator.comparing(Book::getTitle, Comparator.nullsFirst(String::compareTo)))
                .collect(Collectors.toList());
    }

    public List<Book> findByTitleContainingIgnoreCase(String title) {
        if (title == null || title.isBlank()) return findAll();
        String q = title.toLowerCase();
        return storage.values().stream()
                .filter(b -> b.getTitle() != null && b.getTitle().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public List<Book> findByAuthorContainingIgnoreCase(String author) {
        if (author == null || author.isBlank()) return findAll();
        String q = author.toLowerCase();
        return storage.values().stream()
                .filter(b -> b.getAuthor() != null && b.getAuthor().toLowerCase().contains(q))
                .collect(Collectors.toList());
    }

    public synchronized void deleteById(Long id) {
        Book removed = storage.remove(id);
        if (removed != null) {
            isbnIndex.remove(removed.getIsbn());
        }
    }

    public boolean existsByIsbn(String isbn) {
        return isbnIndex.containsKey(isbn);
    }

    public boolean existsByIsbnAndNotId(String isbn, Long id) {
        Long foundId = isbnIndex.get(isbn);
        return foundId != null && !foundId.equals(id);
    }
}
