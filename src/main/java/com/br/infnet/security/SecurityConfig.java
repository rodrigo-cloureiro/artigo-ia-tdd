package com.br.infnet.security;

public class SecurityConfig {
    //Tamanhos máximos e mínimos
    public static final int MAX_CARACTERES_TITULO = 255;
    public static final int MAX_CARACTERES_AUTOR = 100;
    public static final int MAX_ISBN_NUMEROS = 13;
    public static final int MIN_ISBN_NUMEROS = 10;
    public static final int MIN_CARACTERES_TITULO = 1;
    public static final int MIN_CARACTERES_AUTOR = 1;

    //Validações
    public static final boolean VALIDACAO_ESTRITA = true;
    public static final boolean AUTO_SANITIZE = true;
    public static final boolean REJEITAR_ENTRADAS_MALICIOSAS = true;

    //Padrões regex
    public static final String PADRAO_ISBN = "^(?:\\d{9}[\\dX]|\\d{13})$";
    public static final String CARACTERES_VALIDOS_TITULO = "^[\\p{L}\\p{N}\\p{P}\\p{Z}]+$";
    public static final String CARACTERES_VALIDOS_AUTOR = "^[\\p{L}\\p{Z}.,-]+$";

    private SecurityConfig() {
        //Previne a instanciação da classe
    }

    public static void validarTitulo(String title) {
        validarCampo(title, "Título", MIN_CARACTERES_TITULO, MAX_CARACTERES_TITULO);

        if (VALIDACAO_ESTRITA && !title.matches(CARACTERES_VALIDOS_TITULO)) {
            throw new IllegalArgumentException("Título contém caracteres não permitidos");
        }
    }

    public static void validarAutor(String author) {
        validarCampo(author, "Autor", MIN_CARACTERES_AUTOR, MAX_CARACTERES_AUTOR);

        if (VALIDACAO_ESTRITA && !author.matches(CARACTERES_VALIDOS_AUTOR)) {
            throw new IllegalArgumentException("Nome do autor contém caracteres não permitidos");
        }
    }

    public static void validarIsbn(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("ISBN não pode ser nulo ou vazio");
        }

        String isbnLimpo = isbn.replaceAll("[\\s-]", "");

        if (isbnLimpo.length() > MAX_ISBN_NUMEROS) {
            throw new IllegalArgumentException("ISBN muito longo");
        }

        if (isbnLimpo.length() < MIN_ISBN_NUMEROS) {
            throw new IllegalArgumentException("ISBN muito curto");
        }

        if (VALIDACAO_ESTRITA && !isbnLimpo.matches(PADRAO_ISBN)) {
            throw new IllegalArgumentException("Formato de ISBN inválido");
        }
    }

    private static void validarCampo(String field, String fieldName, int minLength, int maxLength) {
        if (field == null) {
            throw new IllegalArgumentException(fieldName + " não pode ser nulo");
        }

        if (REJEITAR_ENTRADAS_MALICIOSAS && TextSanitizer.contemConteudoMalicioso(field)) {
            throw new IllegalArgumentException(fieldName + " contém conteúdo malicioso");
        }

        String campoLimpo = field.trim();

        if (campoLimpo.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " não pode estar vazio");
        }

        if (campoLimpo.length() < minLength) {
            throw new IllegalArgumentException(fieldName + " muito curto (mínimo " + minLength + " caracteres)");
        }

        if (campoLimpo.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " muito longo (máximo " + maxLength + " caracteres)");
        }
    }

    public static String processarEntrada(String input) {
        if (AUTO_SANITIZE) {
            return TextSanitizer.sanitizarEntrada(input);
        }
        return input;
    }
}
