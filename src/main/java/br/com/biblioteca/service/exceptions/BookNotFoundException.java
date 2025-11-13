package br.com.biblioteca.service.exceptions;

public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(Long id) {
        super("Livro não encontrado: id=" + id);
    }
}
