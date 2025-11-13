package br.com.biblioteca.service;

import br.com.biblioteca.dao.BookDAO;
import br.com.biblioteca.model.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes da Lógica de Livros e Regras de Exclusão")
public class BookServiceTest {

    @Mock
    private BookDAO bookDAO;

    @Mock
    private ValidationService validationService;

    @Mock
    private LoanService loanService;

    @InjectMocks
    private BookService bookService;

    private final Long BOOK_ID = 1L;
    private final Book TEST_BOOK = new Book(BOOK_ID, "Título", "Autor", "12345");

    @BeforeEach
    void setUp() {
        // O Mockito já injeta os Mocks no bookService
        bookService.setLoanService(loanService); // Injeção da dependência circular simulada
    }

    // --- Testes de Exclusão (Regra de Negócio) ---

    @Test
    @DisplayName("Deve excluir livro se ele estiver disponível")
    void shouldDeleteBookWhenAvailable() {
        // Configuração: Livro disponível para exclusão
        when(loanService.isBookOnLoan(BOOK_ID)).thenReturn(false);
        when(bookDAO.deleteById(BOOK_ID)).thenReturn(true);

        assertDoesNotThrow(() -> bookService.deleteBook(BOOK_ID));
        verify(bookDAO, times(1)).deleteById(BOOK_ID);
    }

    @Test
    @DisplayName("Não deve excluir livro se estiver emprestado (Regra de Negócio)")
    void shouldNotDeleteBookWhenOnLoan() {
        // Configuração: Livro emprestado
        when(loanService.isBookOnLoan(BOOK_ID)).thenReturn(true);

        assertThrows(IllegalStateException.class, () ->
                bookService.deleteBook(BOOK_ID));

        // Verifica que o metodo de exclusão do DAO nunca foi chamado
        verify(bookDAO, never()).deleteById(BOOK_ID);
    }

    // --- Testes de Adicionar/Atualizar (Integração com Validação) ---

    @Test
    @DisplayName("Deve adicionar livro com sucesso")
    void shouldAddBookSuccessfully() {
        // Mock da validação: faz nada se for válido
        doNothing().when(validationService).validateBook(any(), any(), any());
        when(bookDAO.findByIsbn(any())).thenReturn(Optional.empty());
        when(bookDAO.save(any(Book.class))).thenAnswer(i -> i.getArgument(0)); // Simula o save

        assertDoesNotThrow(() -> bookService.addBook("Novo", "Autor", "54321"));
        verify(bookDAO, times(1)).save(any(Book.class));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar adicionar com ISBN duplicado")
    void shouldThrowOnDuplicateIsbn() {
        doNothing().when(validationService).validateBook(any(), any(), any());
        when(bookDAO.findByIsbn("12345")).thenReturn(Optional.of(TEST_BOOK));

        assertThrows(IllegalArgumentException.class, () ->
                bookService.addBook("Outro", "Autor", "12345"));
        verify(bookDAO, never()).save(any(Book.class));
    }

    @Test
    @DisplayName("Deve atualizar livro com sucesso")
    void shouldUpdateBookSuccessfully() {
        doNothing().when(validationService).validateBook(any(), any(), any());
        when(bookDAO.findById(BOOK_ID)).thenReturn(Optional.of(TEST_BOOK));
        when(bookDAO.findByIsbn("12345")).thenReturn(Optional.of(TEST_BOOK)); // O mesmo livro

        assertDoesNotThrow(() -> bookService.updateBook(BOOK_ID, "Novo Título", "Novo Autor", "12345"));
        verify(bookDAO, times(1)).save(any(Book.class));
    }
}