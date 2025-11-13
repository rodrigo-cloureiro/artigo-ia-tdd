package com.biblioteca.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

class SecurityUtilsTest {

    @Test
    @DisplayName("Deve detectar SQL Injection")
    void deveDetectarSqlInjection() {
        String sqlInjection = "test'; DROP TABLE livros; --";

        SecurityException exception = assertThrows(SecurityException.class,
                () -> SecurityUtils.sanitizeSQL(sqlInjection));

        assertTrue(exception.getMessage().contains("SQL Injection"));
    }

    @Test
    @DisplayName("Deve detectar XSS")
    void deveDetectarXss() {
        String xss = "<script>alert('xss')</script>";

        SecurityException exception = assertThrows(SecurityException.class,
                () -> SecurityUtils.sanitizeXSS(xss));

        assertTrue(exception.getMessage().contains("XSS"));
    }

    @Test
    @DisplayName("Deve sanitizar entrada segura")
    void deveSanitizarEntradaSegura() {
        String entradaSegura = "Dom Casmurro";
        String resultado = SecurityUtils.sanitizeInput(entradaSegura);

        assertEquals("Dom Casmurro", resultado);
    }

    @Test
    @DisplayName("Deve validar ISBN correto")
    void deveValidarIsbnCorreto() {
        assertTrue(SecurityUtils.isValidISBN("9788535930001"));
    }

    @Test
    @DisplayName("Deve rejeitar ISBN incorreto")
    void deveRejeitarIsbnIncorreto() {
        assertFalse(SecurityUtils.isValidISBN("9788535930000"));
    }
}