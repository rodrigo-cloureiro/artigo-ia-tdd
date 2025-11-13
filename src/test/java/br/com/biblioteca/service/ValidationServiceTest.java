package br.com.biblioteca.service;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.domains.Chars;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes de Validação de Segurança")
public class ValidationServiceTest {

    private ValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new ValidationService();
    }

    // --- Testes de Livro ---

    @Test
    @DisplayName("Deve validar um livro com dados corretos")
    void shouldValidateCorrectBook() {
        assertDoesNotThrow(() -> validationService.validateBook(
                "Título Válido", "Autor Válido", "978-8575422465"
        ));
    }

    @Test
    @DisplayName("Deve falhar com título nulo ou vazio")
    void shouldFailWithNullOrEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> validationService.validateBook(
                null, "Autor", "1234567890123"
        ));
        assertThrows(IllegalArgumentException.class, () -> validationService.validateBook(
                " ", "Autor", "1234567890123"
        ));
    }

    @Test
    @DisplayName("Deve falhar com ISBN inválido")
    void shouldFailWithInvalidIsbn() {
        assertThrows(IllegalArgumentException.class, () -> validationService.validateBook(
                "Título", "Autor", "INVALID"
        ));
    }

    // --- Testes de Injeção/XSS (Propriedade) ---

    @Property(tries = 100)
    @DisplayName("Deve detectar caracteres maliciosos em Título (Testes de Propriedade - Jqwik)")
    void shouldDetectDangerousCharactersInTitle(@ForAll @Chars(blacklist = {
            'a', 'b', 'c' // Ignora caracteres comuns para focar em símbolos perigosos
    }) String randomInput) {

        // Foca em strings que contenham explicitamente símbolos de injeção/XSS
        if (randomInput.contains("<") || randomInput.contains("'") || randomInput.contains(";")) {
            assertThrows(IllegalArgumentException.class, () ->
                    validationService.validateBook(randomInput, "Autor", "1234567890123"));
        } else {
            // Caso não haja caracteres perigosos, o teste deve passar
            assertDoesNotThrow(() ->
                    validationService.validateBook("Bom", "Autor", "1234567890123"));
        }
    }

    @Test
    @DisplayName("Deve falhar com entrada de XSS (Ataque XSS)")
    void shouldFailWithXssAttempt() {
        assertThrows(IllegalArgumentException.class, () -> validationService.validateBook(
                "Livro <script>alert('XSS')</script> Ruim", "Autor", "9788535902796"
        ));
        assertThrows(IllegalArgumentException.class, () -> validationService.validateBorrowerName(
                "Leitor '; DROP TABLE users;"
        ));
    }
}