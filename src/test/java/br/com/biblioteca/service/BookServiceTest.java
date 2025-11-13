package br.com.biblioteca.service;

import br.com.biblioteca.dao.BookDAO;
import br.com.biblioteca.model.Book;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para a camada de Serviço (BookService).
 * Utiliza Mockito para simular (mockar) as dependências (DAO e ValidationService).
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookDAO bookDAO; // Mock da camada de dados

    @Mock
    private ValidationService validationService; // Mock da camada de validação

    @InjectMocks
    private BookService bookService; // A classe que estamos testando

    private final String validTitle = "Título Válido";
    private final String validAuthor = "Autor Válido";
    private final String validIsbn = "1234567890123";
    private final Book sampleBook = new Book(1L, validTitle, validAuthor, validIsbn);

    // --- Testes de addBook ---

    @Test
    @DisplayName("addBook: Deve adicionar um livro com sucesso")
    void addBook_Success() {
        // 1. Configuração do Mock
        // Não faz nada quando a validação é chamada (não lança exceção)
        doNothing().when(validationService).validateBook(validTitle, validAuthor, validIsbn);

        // Retorna "vazio" quando o DAO procura pelo ISBN (ou seja, ISBN está livre)
        when(bookDAO.findByIsbn(validIsbn)).thenReturn(Optional.empty());

        // Retorna o livro salvo quando o DAO.save é chamado com um livro sem ID
        when(bookDAO.save(any(Book.class))).thenAnswer(invocation -> {
            Book b = invocation.getArgument(0);
            // Simula o DAO atribuindo um ID
            return new Book(1L, b.title(), b.author(), b.isbn());
        });

        // 2. Execução
        Book result = bookService.addBook(validTitle, validAuthor, validIsbn);

        // 3. Verificação
        assertNotNull(result);
        assertEquals(1L, result.id());

        // Verifica se os mocks foram chamados como esperado
        verify(validationService).validateBook(validTitle, validAuthor, validIsbn);
        verify(bookDAO).findByIsbn(validIsbn);
        verify(bookDAO).save(any(Book.class));
    }

    @Test
    @DisplayName("addBook: Deve falhar se a validação falhar")
    void addBook_FailsOnValidation() {
        String invalidIsbn = "123";
        // Configura o mock de validação para lançar uma exceção
        doThrow(new IllegalArgumentException("ISBN inválido"))
                .when(validationService).validateBook(validTitle, validAuthor, invalidIsbn);

        // Execução e Verificação
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.addBook(validTitle, validAuthor, invalidIsbn);
        });

        assertEquals("ISBN inválido", exception.getMessage());

        // Garante que o DAO nunca foi chamado
        verify(bookDAO, never()).findByIsbn(anyString());
        verify(bookDAO, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("addBook: Deve falhar se o ISBN já existir")
    void addBook_FailsOnDuplicateIsbn() {
        // Configuração
        doNothing().when(validationService).validateBook(validTitle, validAuthor, validIsbn);
        // Simula que o ISBN já existe
        when(bookDAO.findByIsbn(validIsbn)).thenReturn(Optional.of(sampleBook));

        // Execução e Verificação
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.addBook(validTitle, validAuthor, validIsbn);
        });

        assertTrue(exception.getMessage().contains("já está cadastrado"));

        // Garante que o save nunca foi chamado
        verify(bookDAO, never()).save(any(Book.class));
    }

    // --- Testes de updateBook ---

    @Test
    @DisplayName("updateBook: Deve atualizar um livro com sucesso")
    void updateBook_Success() {
        Long id = 1L;
        String newTitle = "Novo Título";

        // Configuração
        doNothing().when(validationService).validateBook(newTitle, validAuthor, validIsbn);
        // Simula que o livro a ser atualizado existe
        when(bookDAO.findById(id)).thenReturn(Optional.of(sampleBook));
        // Simula que o ISBN (o mesmo) pertence a este livro
        when(bookDAO.findByIsbn(validIsbn)).thenReturn(Optional.of(sampleBook));

        // Execução
        bookService.updateBook(id, newTitle, validAuthor, validIsbn);

        // Verificação
        verify(validationService).validateBook(newTitle, validAuthor, validIsbn);
        verify(bookDAO).findById(id);
        verify(bookDAO).findByIsbn(validIsbn);
        // Verifica se o save foi chamado com os dados corretos
        verify(bookDAO).save(eq(new Book(id, newTitle, validAuthor, validIsbn)));
    }

    @Test
    @DisplayName("updateBook: Deve falhar se o livro não existir")
    void updateBook_FailsIfBookNotFound() {
        Long id = 99L;
        // Configuração
        doNothing().when(validationService).validateBook(validTitle, validAuthor, validIsbn);
        // Simula que o livro não foi encontrado
        when(bookDAO.findById(id)).thenReturn(Optional.empty());

        // Execução e Verificação
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.updateBook(id, validTitle, validAuthor, validIsbn);
        });

        assertTrue(exception.getMessage().contains("não encontrado"));
        verify(bookDAO, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("updateBook: Deve falhar se o novo ISBN já pertencer a outro livro")
    void updateBook_FailsIfIsbnBelongsToOtherBook() {
        Long idBook1 = 1L;
        Book book1 = new Book(idBook1, "Livro 1", "Autor 1", "1111111111111");
        Book book2 = new Book(2L, "Livro 2", "Autor 2", "2222222222222");

        String newIsbnForBook1 = "2222222222222"; // Tentando usar o ISBN do livro 2

        // Configuração
        doNothing().when(validationService).validateBook(validTitle, validAuthor, newIsbnForBook1);
        when(bookDAO.findById(idBook1)).thenReturn(Optional.of(book1));
        // Simula que o novo ISBN (do livro 2) foi encontrado e pertence ao livro 2
        when(bookDAO.findByIsbn(newIsbnForBook1)).thenReturn(Optional.of(book2));

        // Execução e Verificação
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.updateBook(idBook1, validTitle, validAuthor, newIsbnForBook1);
        });

        assertTrue(exception.getMessage().contains("já está cadastrado para outro livro"));
        verify(bookDAO, never()).save(any(Book.class));
    }

    // --- Testes de deleteBook ---

    @Test
    @DisplayName("deleteBook: Deve excluir com sucesso")
    void deleteBook_Success() {
        Long id = 1L;
        // Configuração: Simula que o DAO excluiu com sucesso
        when(bookDAO.deleteById(id)).thenReturn(true);

        // Execução
        bookService.deleteBook(id);

        // Verificação
        verify(bookDAO).deleteById(id);
    }

    @Test
    @DisplayName("deleteBook: Deve falhar se o livro não existir")
    void deleteBook_FailsIfNotFound() {
        Long id = 99L;
        // Configuração: Simula que o DAO não encontrou o livro para excluir
        when(bookDAO.deleteById(id)).thenReturn(false);

        // Execução e Verificação
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.deleteBook(id);
        });

        assertTrue(exception.getMessage().contains("não encontrado"));
    }
}