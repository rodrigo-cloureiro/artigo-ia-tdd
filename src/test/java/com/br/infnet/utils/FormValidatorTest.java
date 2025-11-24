package com.br.infnet.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FormValidatorTest {

    private Map<String, String> validParams;

    @BeforeEach
    void setUp() {
        validParams = new HashMap<>();
        validParams.put("titulo", "Clean Code");
        validParams.put("autor", "Robert C. Martin");
        validParams.put("isbn", "1234567890123");
    }

    @Test
    @DisplayName("Deve validar livro com dados válidos")
    void testValidarLivroValido() {
        FormValidator.ValidationResult result = FormValidator.validateLivro(validParams);

        assertTrue(result.isValid());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar livro sem título")
    void testValidarLivroSemTitulo() {
        validParams.remove("titulo");

        FormValidator.ValidationResult result = FormValidator.validateLivro(validParams);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Título é obrigatório"));
    }

    @Test
    @DisplayName("Deve rejeitar livro sem autor")
    void testValidarLivroSemAutor() {
        validParams.remove("autor");

        FormValidator.ValidationResult result = FormValidator.validateLivro(validParams);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Autor é obrigatório"));
    }

    @Test
    @DisplayName("Deve rejeitar livro sem ISBN")
    void testValidarLivroSemIsbn() {
        validParams.remove("isbn");

        FormValidator.ValidationResult result = FormValidator.validateLivro(validParams);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("ISBN é obrigatório"));
    }

    @Test
    @DisplayName("Deve validar prazo válido")
    void testValidarPrazoValido() {
        FormValidator.ValidationResult result = FormValidator.validatePrazo("30");

        assertTrue(result.isValid());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar prazo nulo")
    void testValidarPrazoNulo() {
        FormValidator.ValidationResult result = FormValidator.validatePrazo(null);

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Prazo é obrigatório"));
    }

    @Test
    @DisplayName("Deve rejeitar prazo menor que 1")
    void testValidarPrazoMenorQueUm() {
        FormValidator.ValidationResult result = FormValidator.validatePrazo("0");

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Prazo deve ser pelo menos 1 dia"));
    }

    @Test
    @DisplayName("Deve rejeitar prazo maior que 365")
    void testValidarPrazoMaiorQue365() {
        FormValidator.ValidationResult result = FormValidator.validatePrazo("400");

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Prazo não pode exceder 1 ano"));
    }

    @Test
    @DisplayName("Deve validar busca válida")
    void testValidarBuscaValida() {
        FormValidator.ValidationResult result = FormValidator.validateBusca("titulo", "Clean Code");

        assertTrue(result.isValid());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar busca com tipo inválido")
    void testValidarBuscaTipoInvalido() {
        FormValidator.ValidationResult result = FormValidator.validateBusca("categoria", "ficção");

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("Tipo de busca inválido"));
    }

    @Test
    @DisplayName("Deve validar busca por ID válido")
    void testValidarBuscaPorIdValido() {
        FormValidator.ValidationResult result = FormValidator.validateBusca("id", "123");

        assertTrue(result.isValid());
        assertTrue(result.errors().isEmpty());
    }

    @Test
    @DisplayName("Deve rejeitar busca por ID inválido")
    void testValidarBuscaPorIdInvalido() {
        FormValidator.ValidationResult result = FormValidator.validateBusca("id", "abc");

        assertFalse(result.isValid());
        assertTrue(result.getErrorMessage().contains("ID deve ser um número válido"));
    }
}
