package br.com.biblioteca.service;

import br.com.biblioteca.dao.BookDAO;
import br.com.biblioteca.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Camada de Serviço (Service Layer) para a entidade Livro.
 */
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    private final BookDAO bookDAO;
    private final ValidationService validationService;
    private LoanService loanService; // Dependência para verificação de empréstimo

    public BookService(BookDAO bookDAO, ValidationService validationService) {
        this.bookDAO = bookDAO;
        this.validationService = validationService;
    }

    /**
     * Permite a injeção do LoanService após a construção.
     */
    public void setLoanService(LoanService loanService) {
        this.loanService = loanService;
    }

    // ... (Métodos addBook, updateBook, getBookById, getAllBooks, searchBooks mantidos)

    /**
     * Exclui um livro pelo ID.
     * **REGRA: Livros emprestados não podem ser excluídos.**
     *
     * @param id O ID do livro.
     * @throws IllegalStateException se o livro estiver emprestado.
     */
    public void deleteBook(Long id) {
        // **NOVA VERIFICAÇÃO:**
        if (loanService != null && loanService.isBookOnLoan(id)) {
            throw new IllegalStateException("O livro não pode ser excluído, pois está atualmente emprestado.");
        }

        boolean deleted = bookDAO.deleteById(id);
        if (!deleted) {
            throw new IllegalArgumentException("Livro com ID " + id + " não encontrado.");
        }
        logger.info("Livro excluído com sucesso: id={}", id);
    }

    public Book addBook(String title, String author, String isbn) {
        validationService.validateBook(title, author, isbn);

        if (bookDAO.findByIsbn(isbn).isPresent()) {
            throw new IllegalArgumentException("O ISBN " + isbn + " já está cadastrado.");
        }

        Book newBook = new Book(title, author, isbn);
        return bookDAO.save(newBook);
    }

    public Book updateBook(Long id, String title, String author, String isbn) {
        validationService.validateBook(title, author, isbn);

        bookDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Livro com ID " + id + " não encontrado."));

        Optional<Book> bookWithIsbn = bookDAO.findByIsbn(isbn);
        if (bookWithIsbn.isPresent() && !bookWithIsbn.get().id().equals(id)) {
            throw new IllegalArgumentException("O ISBN " + isbn + " já está cadastrado para outro livro.");
        }

        Book updatedBook = new Book(id, title, author, isbn);
        return bookDAO.save(updatedBook);
    }

    public Book getBookById(Long id) {
        return bookDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Livro com ID " + id + " não encontrado."));
    }

    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }

    public List<Book> searchBooks(String query) {
        return bookDAO.search(query);
    }
}