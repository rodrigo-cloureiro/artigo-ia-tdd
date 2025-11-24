package com.br.infnet.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorHandlerTest {

    private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private final PrintStream originalErr = System.err;

    @BeforeEach
    void setUp() {
        System.setErr(new PrintStream(errContent));
    }

    @AfterEach
    void tearDown() {
        System.setErr(originalErr);
    }

    @Test
    @DisplayName("Deve tratar exceção geral")
    void testHandleError() {
        Exception e = new RuntimeException("Erro de teste");

        String result = ErrorHandler.handleError(e);

        assertNotNull(result);
        assertTrue(errContent.toString().contains("RuntimeException"));
        assertTrue(errContent.toString().contains("Erro de teste"));
    }

    @Test
    @DisplayName("Deve tratar NumberFormatException")
    void testHandleNumberFormatException() {
        NumberFormatException e = new NumberFormatException("Formato inválido");

        String result = ErrorHandler.handleError(e);

        assertNotNull(result);
        assertTrue(errContent.toString().contains("NumberFormatException"));
    }

    @Test
    @DisplayName("Deve tratar erro de validação")
    void testHandleValidationError() {
        String result = ErrorHandler.handleValidationError("Campo obrigatório");

        assertNotNull(result);
        assertTrue(result.contains("Dados inválidos"));
    }

    @Test
    @DisplayName("Deve tratar livro não encontrado")
    void testHandleNotFoundLivro() {
        String result = ErrorHandler.handleNotFound("livro");

        assertNotNull(result);
        assertTrue(result.contains("Livro não encontrado"));
    }

    @Test
    @DisplayName("Deve tratar empréstimo não encontrado")
    void testHandleNotFoundEmprestimo() {
        String result = ErrorHandler.handleNotFound("emprestimo");

        assertNotNull(result);
        assertTrue(result.contains("Empréstimo não encontrado"));
    }

    @Test
    @DisplayName("Deve tratar recurso genérico não encontrado")
    void testHandleNotFoundGenerico() {
        String result = ErrorHandler.handleNotFound("outro");

        assertNotNull(result);
        assertTrue(result.contains("Recurso não encontrado"));
    }

    @Test
    @DisplayName("Deve tratar erro de banco de dados")
    void testHandleDatabaseError() {
        String result = ErrorHandler.handleDatabaseError();

        assertNotNull(result);
        assertTrue(result.contains("Erro interno do sistema"));
    }

    @Test
    @DisplayName("Deve tratar erro de regra de negócio")
    void testHandleBusinessLogicError() {
        String result = ErrorHandler.handleBusinessLogicError("Livro já emprestado");

        assertNotNull(result);
        assertTrue(result.contains("Operação não permitida"));
        assertTrue(result.contains("Livro já emprestado"));
    }
}
