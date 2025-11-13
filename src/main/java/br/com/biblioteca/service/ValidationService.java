package br.com.biblioteca.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Serviço dedicado para validar as regras de negócio da entidade Book.
 * Desacopla a lógica de validação do serviço principal.
 */
public class ValidationService {

    // Regex para validar se a string contém apenas dígitos
    private static final Pattern DIGITS_ONLY = Pattern.compile("^\\d+$");

    /**
     * Valida os campos de um livro (título, autor, ISBN).
     *
     * @param title  Título do livro.
     * @param author Autor do livro.
     * @param isbn   ISBN do livro.
     * @throws IllegalArgumentException se qualquer regra de validação for violada.
     */
    public void validateBook(String title, String author, String isbn) {
        List<String> errors = new ArrayList<>();

        if (isNullOrBlank(title)) {
            errors.add("O título não pode ser nulo ou vazio.");
        }
        if (isNullOrBlank(author)) {
            errors.add("O autor não pode ser nulo ou vazio.");
        }

        validateIsbn(isbn, errors);

        if (!errors.isEmpty()) {
            // Junta todas as mensagens de erro em uma única exceção
            throw new IllegalArgumentException(String.join(" ", errors));
        }
    }

    private void validateIsbn(String isbn, List<String> errors) {
        if (isNullOrBlank(isbn)) {
            errors.add("O ISBN não pode ser nulo ou vazio.");
            return; // Não continua se for nulo
        }

        if (isbn.length() != 13) {
            errors.add("O ISBN deve ter exatamente 13 dígitos.");
        }

        if (!DIGITS_ONLY.matcher(isbn).matches()) {
            errors.add("O ISBN deve conter apenas números.");
        }

        // Adicional (opcional): Validação do dígito verificador do ISBN-13
        // Para este exemplo, as verificações de tamanho e dígitos são suficientes.
    }

    private boolean isNullOrBlank(String str) {
        return Objects.isNull(str) || str.isBlank();
    }
}