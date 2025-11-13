package br.com.biblioteca.service;

import java.util.regex.Pattern;

/**
 * Serviço dedicado a validar e sanitizar dados de entrada para prevenir ataques.
 */
public class ValidationService {

    // Padrão que identifica caracteres perigosos comuns em injeção de comandos/SQL/XSS
    // Exemplos: <, >, ', ", ;, --, ( ). Usado para verificar entradas de texto.
    private static final Pattern DANGEROUS_CHARACTERS = Pattern.compile("[<>'\"\\(\\)\\;\\-\\-]");

    /**
     * Valida os campos de um livro e sanitiza as entradas.
     * @throws IllegalArgumentException se a validação falhar.
     */
    public void validateBook(String title, String author, String isbn) {
        if (title == null || title.isBlank() || title.length() > 255) {
            throw new IllegalArgumentException("Título inválido: deve ter entre 1 e 255 caracteres.");
        }
        if (author == null || author.isBlank() || author.length() > 255) {
            throw new IllegalArgumentException("Autor inválido: deve ter entre 1 e 255 caracteres.");
        }
        // ISBN deve ser uma sequência numérica (10 ou 13 dígitos)
        if (isbn == null || !isbn.matches("^[0-9xX\\-]{10,17}$")) {
            throw new IllegalArgumentException("ISBN inválido: formato incorreto.");
        }

        // Aplica sanitização de entrada
        if (containsDangerousCharacters(title) || containsDangerousCharacters(author)) {
            throw new IllegalArgumentException("Entrada maliciosa detectada nos campos Título/Autor.");
        }
    }

    /**
     * Valida e sanitiza o nome do leitor.
     */
    public void validateBorrowerName(String name) {
        if (name == null || name.isBlank() || name.length() > 100) {
            throw new IllegalArgumentException("Nome do leitor inválido.");
        }
        if (containsDangerousCharacters(name)) {
            throw new IllegalArgumentException("Entrada maliciosa detectada no nome do leitor.");
        }
    }

    /**
     * Verifica se a string contém caracteres potencialmente perigosos.
     */
    private boolean containsDangerousCharacters(String input) {
        if (input == null) return false;
        // Sanitiza a entrada, removendo tags HTML e caracteres perigosos
        String sanitized = input.replaceAll("<[^>]*>", "") // Remove tags HTML
                .replaceAll("\\s+", " ").trim(); // Normaliza espaços

        return DANGEROUS_CHARACTERS.matcher(sanitized).find();
    }
}