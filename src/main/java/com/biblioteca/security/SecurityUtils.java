package com.biblioteca.security;

import org.owasp.encoder.Encode;
import java.util.regex.Pattern;

public class SecurityUtils {

    // Padrões de validação
    private static final Pattern SQL_INJECTION_PATTERN =
            Pattern.compile("(?i)(\\b(SELECT|INSERT|UPDATE|DELETE|DROP|UNION|EXEC|ALTER|CREATE|TRUNCATE)\\b|;|--|/\\*|\\*/)");

    private static final Pattern XSS_PATTERN =
            Pattern.compile("(?i)(<script|javascript:|onclick|onload|onerror|eval\\(|alert\\(|document\\.cookie)");

    private static final Pattern HTML_TAGS_PATTERN =
            Pattern.compile("<[^>]*>");

    private static final Pattern VALID_TITLE_PATTERN =
            Pattern.compile("^[a-zA-Z0-9\\sáàâãéèêíïóôõöúçñÁÀÂÃÉÈÊÍÏÓÔÕÖÚÇÑ.,!?;:()\\-]{1,255}$");

    private static final Pattern VALID_AUTHOR_PATTERN =
            Pattern.compile("^[a-zA-Z\\sáàâãéèêíïóôõöúçñÁÀÂÃÉÈÊÍÏÓÔÕÖÚÇÑ\\-']{1,100}$");

    private static final Pattern VALID_USERNAME_PATTERN =
            Pattern.compile("^[a-zA-Z0-9\\s\\-.]{3,50}$");

    // Prevenção contra SQL Injection
    public static String sanitizeSQL(String input) {
        if (input == null) return null;

        if (SQL_INJECTION_PATTERN.matcher(input).find()) {
            throw new SecurityException("Entrada contém padrões suspeitos de SQL Injection");
        }

        return input.trim();
    }

    // Prevenção contra XSS
    public static String sanitizeXSS(String input) {
        if (input == null) return null;

        if (XSS_PATTERN.matcher(input).find()) {
            throw new SecurityException("Entrada contém padrões suspeitos de XSS");
        }

        // Remove tags HTML
        String sanitized = HTML_TAGS_PATTERN.matcher(input).replaceAll("");
        return Encode.forHtml(sanitized);
    }

    // Sanitização geral para entradas de usuário
    public static String sanitizeInput(String input) {
        if (input == null) return null;

        input = sanitizeSQL(input);
        input = sanitizeXSS(input);

        return input.trim();
    }

    // Validação de título
    public static boolean isValidTitle(String title) {
        return title != null && VALID_TITLE_PATTERN.matcher(title).matches();
    }

    // Validação de autor
    public static boolean isValidAuthor(String author) {
        return author != null && VALID_AUTHOR_PATTERN.matcher(author).matches();
    }

    // Validação de nome de usuário
    public static boolean isValidUsername(String username) {
        return username != null && VALID_USERNAME_PATTERN.matcher(username).matches();
    }

    // Validação de ISBN
    public static boolean isValidISBN(String isbn) {
        if (isbn == null || !isbn.matches("^\\d{13}$")) {
            return false;
        }

        // Algoritmo de validação ISBN-13
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            int digit = Character.getNumericValue(isbn.charAt(i));
            sum += (i % 2 == 0) ? digit : digit * 3;
        }

        int checksum = 10 - (sum % 10);
        if (checksum == 10) checksum = 0;

        return checksum == Character.getNumericValue(isbn.charAt(12));
    }

    // Validação de prazo de empréstimo
    public static boolean isValidLoanPeriod(int days) {
        return days > 0 && days <= 30; // Máximo de 30 dias por segurança
    }

    // Escape para conteúdo HTML
    public static String escapeHtml(String input) {
        return input != null ? Encode.forHtmlContent(input) : "";
    }

    // Escape para atributos HTML
    public static String escapeHtmlAttribute(String input) {
        return input != null ? Encode.forHtmlAttribute(input) : "";
    }
}