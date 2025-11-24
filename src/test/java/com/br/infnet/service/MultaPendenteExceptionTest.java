package com.br.infnet.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MultaPendenteExceptionTest {

    @Test
    @DisplayName("Deve criar exceção com mensagem")
    void testCriarExcecaoComMensagem() {
        String mensagem = "Multa de R$ 15,00 pendente";

        MultaPendenteException exception = new MultaPendenteException(mensagem);

        assertEquals(mensagem, exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    @DisplayName("Deve ser uma RuntimeException")
    void testTipoExcecao() {
        MultaPendenteException exception = new MultaPendenteException("Teste");

        assertInstanceOf(IllegalStateException.class, exception);
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    @DisplayName("Deve permitir mensagem nula")
    void testMensagemNula() {
        assertDoesNotThrow(() -> {
            MultaPendenteException exception = new MultaPendenteException(null);
            assertNull(exception.getMessage());
        });
    }

    @Test
    @DisplayName("Deve poder ser lançada e capturada")
    void testLancarECapturar() {
        String mensagem = "Erro de teste";

        assertThrows(MultaPendenteException.class, () -> {
            throw new MultaPendenteException(mensagem);
        });

        Exception exception = assertThrows(MultaPendenteException.class, () -> {
            throw new MultaPendenteException(mensagem);
        });

        assertEquals(mensagem, exception.getMessage());
    }
}
