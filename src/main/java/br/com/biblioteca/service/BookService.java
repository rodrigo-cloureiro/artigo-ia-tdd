package br.com.biblioteca.service;

import br.com.biblioteca.dao.BookDAO;
import br.com.biblioteca.model.Book;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Camada de Serviço (Service Layer) para a entidade Livro.
 * Contém a lógica de negócio e orquestra as interações com o DAO.
 */
public class BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    private final BookDAO bookDAO;
    private final ValidationService validationService;

    public BookService(BookDAO bookDAO, ValidationService validationService) {
        this.bookDAO = bookDAO;
        this.validationService = validationService;
    }

    /**
     * Adiciona um novo livro ao sistema.
     *
     * @param title  Título
     * @param author Autor
     * @param isbn   ISBN
     * @return O livro criado e salvo.
     * @throws IllegalArgumentException se a validação falhar ou o ISBN já existir.
     */
    public Book addBook(String title, String author, String isbn) {
        logger.debug("Tentando adicionar livro: isbn={}", isbn);

        // 1. Validar campos
        validationService.validateBook(title, author, isbn);

        // 2. Validar regra de negócio: ISBN deve ser único
        if (bookDAO.findByIsbn(isbn).isPresent()) {
            logger.warn("Falha ao adicionar: ISBN {} já existe.", isbn);
            throw new IllegalArgumentException("O ISBN " + isbn + " já está cadastrado.");
        }

        // 3. Criar e salvar
        Book newBook = new Book(title, author, isbn);
        Book savedBook = bookDAO.save(newBook);

        logger.info("Livro adicionado com sucesso: id={}", savedBook.id());
        return savedBook;
    }

    /**
     * Atualiza um livro existente.
     *
     * @param id     ID do livro a ser atualizado.
     * @param title  Novo título.
     * @param author Novo autor.
     * @param isbn   Novo ISBN.
     * @return O livro atualizado.
     * @throws IllegalArgumentException se a validação falhar, o livro não existir ou o novo ISBN já pertencer a outro livro.
     */
    public Book updateBook(Long id, String title, String author, String isbn) {
        logger.debug("Tentando atualizar livro: id={}", id);

        // 1. Validar campos
        validationService.validateBook(title, author, isbn);

        // 2. Verificar se o livro existe
        Book existingBook = bookDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Livro com ID " + id + " não encontrado."));

        // 3. Validar regra de negócio: ISBN deve ser único (ou pertencer ao próprio livro)
        Optional<Book> bookWithIsbn = bookDAO.findByIsbn(isbn);
        if (bookWithIsbn.isPresent() && !bookWithIsbn.get().id().equals(id)) {
            logger.warn("Falha ao atualizar: ISBN {} já pertence a outro livro (id={}).", isbn, bookWithIsbn.get().id());
            throw new IllegalArgumentException("O ISBN " + isbn + " já está cadastrado para outro livro.");
        }

        // 4. Criar e salvar
        Book updatedBook = new Book(id, title, author, isbn);
        bookDAO.save(updatedBook);

        logger.info("Livro atualizado com sucesso: id={}", id);
        return updatedBook;
    }

    /**
     * Exclui um livro pelo ID.
     *
     * @param id O ID do livro.
     * @throws IllegalArgumentException se o livro não for encontrado.
     */
    public void deleteBook(Long id) {
        logger.debug("Tentando excluir livro: id={}", id);
        boolean deleted = bookDAO.deleteById(id);
        if (!deleted) {
            logger.warn("Falha ao excluir: Livro com ID {} não encontrado.", id);
            throw new IllegalArgumentException("Livro com ID " + id + " não encontrado.");
        }
        logger.info("Livro excluído com sucesso: id={}", id);
    }

    /**
     * Busca um livro pelo ID.
     *
     * @param id O ID.
     * @return O livro encontrado.
     * @throws IllegalArgumentException se o livro não for encontrado.
     */
    public Book getBookById(Long id) {
        return bookDAO.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Livro com ID " + id + " não encontrado."));
    }

    /**
     * Retorna todos os livros.
     *
     * @return Lista de todos os livros.
     */
    public List<Book> getAllBooks() {
        return bookDAO.findAll();
    }

    /**
     * Busca livros por um termo de pesquisa.
     *
     * @param query O termo (busca em título, autor ou ISBN).
     * @return Lista de livros correspondentes.
     */
    public List<Book> searchBooks(String query) {
        return bookDAO.search(query);
    }
}