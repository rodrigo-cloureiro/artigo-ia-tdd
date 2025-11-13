package br.com.biblioteca.dao;

import br.com.biblioteca.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryBookDAOTest {

    private BookDAO bookDAO;

    @BeforeEach
    void setUp() {
        // Cria uma nova instância limpa para cada teste
        bookDAO = new InMemoryBookDAO();
    }

    @Test
    @DisplayName("Deve salvar um novo livro e atribuir um ID")
    void save_NewBook_ShouldAssignId() {
        Book bookToSave = new Book("Novo Livro", "Novo Autor", "1234567890123");
        assertNull(bookToSave.id());

        Book savedBook = bookDAO.save(bookToSave);

        assertNotNull(savedBook.id());
        assertEquals(1L, savedBook.id());
        assertEquals("Novo Livro", savedBook.title());
    }

    @Test
    @DisplayName("Deve atualizar um livro existente")
    void save_ExistingBook_ShouldUpdate() {
        // 1. Salva um livro novo
        Book newBook = bookDAO.save(new Book("Livro Antigo", "Autor Antigo", "1111111111111"));
        Long id = newBook.id();

        // 2. Cria uma versão atualizada
        Book bookToUpdate = new Book(id, "Livro Atualizado", "Autor Atualizado", "2222222222222");

        // 3. Salva a atualização
        Book updatedBook = bookDAO.save(bookToUpdate);

        assertEquals(id, updatedBook.id());
        assertEquals("Livro Atualizado", updatedBook.title());
        assertEquals("Autor Atualizado", updatedBook.author());

        // 4. Verifica se o DAO tem apenas 1 item
        assertEquals(1, bookDAO.findAll().size());
        assertEquals("Livro Atualizado", bookDAO.findById(id).get().title());
    }

    @Test
    @DisplayName("Deve encontrar um livro pelo ID")
    void findById_ShouldReturnBook() {
        Book savedBook = bookDAO.save(new Book("Teste", "Autor", "1234567890123"));

        Optional<Book> foundBook = bookDAO.findById(savedBook.id());

        assertTrue(foundBook.isPresent());
        assertEquals(savedBook.id(), foundBook.get().id());
    }

    @Test
    @DisplayName("Não deve encontrar um livro com ID inexistente")
    void findById_Inexistent_ShouldReturnEmpty() {
        Optional<Book> foundBook = bookDAO.findById(999L);
        assertTrue(foundBook.isEmpty());
    }

    @Test
    @DisplayName("Deve encontrar um livro pelo ISBN")
    void findByIsbn_ShouldReturnBook() {
        String isbn = "9876543210987";
        bookDAO.save(new Book("Teste ISBN", "Autor", isbn));

        Optional<Book> foundBook = bookDAO.findByIsbn(isbn);

        assertTrue(foundBook.isPresent());
        assertEquals(isbn, foundBook.get().isbn());
    }

    @Test
    @DisplayName("Deve retornar todos os livros")
    void findAll_ShouldReturnAllBooks() {
        bookDAO.save(new Book("Livro 1", "Autor 1", "1111111111111"));
        bookDAO.save(new Book("Livro 2", "Autor 2", "2222222222222"));

        List<Book> allBooks = bookDAO.findAll();

        assertEquals(2, allBooks.size());
    }

    @Test
    @DisplayName("Deve excluir um livro pelo ID")
    void deleteById_ShouldRemoveBook() {
        Book savedBook = bookDAO.save(new Book("Para Excluir", "Autor", "3333333333333"));
        Long id = savedBook.id();

        assertTrue(bookDAO.findById(id).isPresent()); // Verifica se existe

        boolean deleted = bookDAO.deleteById(id);

        assertTrue(deleted);
        assertTrue(bookDAO.findById(id).isEmpty()); // Verifica se foi excluído
        assertEquals(0, bookDAO.findAll().size());
    }

    @Test
    @DisplayName("Deve retornar false ao tentar excluir ID inexistente")
    void deleteById_Inexistent_ShouldReturnFalse() {
        boolean deleted = bookDAO.deleteById(999L);
        assertFalse(deleted);
    }

    @Test
    @DisplayName("Deve buscar livros por título, autor ou ISBN")
    void search_ShouldFindMatchingBooks() {
        bookDAO.save(new Book("Java para Iniciantes", "Herbert", "1000000000001"));
        bookDAO.save(new Book("Código Limpo", "Robert Martin", "1000000000002"));
        bookDAO.save(new Book("Javalin em Ação", "Algum Autor", "1000000000003"));

        // Busca por título
        assertEquals(1, bookDAO.search("limpo").size());
        // Busca por autor
        assertEquals(1, bookDAO.search("Herbert").size());
        // Busca por ISBN
        assertEquals(1, bookDAO.search("000003").size());
        // Busca genérica
        assertEquals(2, bookDAO.search("Java").size());
        // Busca vazia (retorna todos)
        assertEquals(3, bookDAO.search("").size());
        assertEquals(3, bookDAO.search(null).size());
    }
}