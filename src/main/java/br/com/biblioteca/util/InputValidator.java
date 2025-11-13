package br.com.biblioteca.util;

import java.util.regex.Pattern;

public final class InputValidator {

    private static final Pattern ISBN13 = Pattern.compile("^\\d{13}$");
    private static final int MAX_LEN = 255;

    private InputValidator() {}

    public static void requireNonEmpty(String field, String fieldName) {
        if (field == null || field.isBlank()) throw new IllegalArgumentException(fieldName + " é obrigatório");
        if (field.length() > MAX_LEN) throw new IllegalArgumentException(fieldName + " excede comprimento máximo");
    }

    public static void validateIsbn(String isbn) {
        if (isbn == null) throw new IllegalArgumentException("ISBN é obrigatório");
        if (!ISBN13.matcher(isbn).matches()) throw new IllegalArgumentException("ISBN deve conter exatamente 13 dígitos numéricos");
    }
}
