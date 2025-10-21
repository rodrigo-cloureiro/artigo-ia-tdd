package com.br.infnet.security;

import java.util.regex.Pattern;

public class TextSanitizer {

    // Padrões maliciosos comuns
    private static final Pattern PADRAO_SCRIPT = Pattern.compile("(?i)<\\s*script[^>]*>.*?</\\s*script\\s*>", Pattern.DOTALL);
    private static final Pattern PADRAO_JAVASCRIPT = Pattern.compile("(?i)javascript\\s*:", Pattern.DOTALL);
    private static final Pattern PADRAO_INJECAO_SQL = Pattern.compile("(?i)(DROP|DELETE|INSERT|UPDATE|SELECT|UNION|EXEC|EXECUTE|ALTER|CREATE|TRUNCATE)(?=\\s|$|;|--|/\\*)", Pattern.DOTALL);
    private static final Pattern PADRAO_PATH_TRAVERSAL = Pattern.compile("(?i)(\\.\\.[\\\\/]|[\\\\/]\\.\\.)", Pattern.DOTALL);
    private static final Pattern PADRAO_CARACTERES_CONTROLE = Pattern.compile("[\\x00-\\x1F\\x7F]");
    private static final Pattern PADRAO_HTML_TAGS = Pattern.compile("<[^>]+>");
    private static final Pattern PADRAO_SQL_COMMENTS = Pattern.compile("--.*$|/\\*.*?\\*/", Pattern.DOTALL | Pattern.MULTILINE);

    //Padrões para caracteres especiais perigosos
    private static final Pattern PADRAO_ASPAS_PERIGOSAS = Pattern.compile("['\";]");
    private static final Pattern PADRAO_SIMBOLOS_PERIGOSOS = Pattern.compile("[<>&\"']");

    private TextSanitizer() {
        // Previne instanciação
    }

    public static boolean contemConteudoMalicioso(String input) {
        if (input == null) {
            return false;
        }

        //Testa os padrões
        return PADRAO_SCRIPT.matcher(input).find() ||
                PADRAO_JAVASCRIPT.matcher(input).find() ||
                PADRAO_INJECAO_SQL.matcher(input).find() ||
                PADRAO_PATH_TRAVERSAL.matcher(input).find() ||
                PADRAO_CARACTERES_CONTROLE.matcher(input).find() ||
                input.trim().isEmpty() && !input.isEmpty() || // Apenas espaços
                input.contains("UNION SELECT") ||
                input.contains("OR '1'='1") ||
                input.contains("<img") ||
                input.contains("onerror") ||
                input.contains("alert(") ||
                input.contains("@#$%&*()+={}[]|\\:;\"<>?/~`");
    }

    public static String sanitizarEntrada(String input) {
        if (input == null) {
            return null;
        }

        String resultado = PADRAO_SCRIPT.matcher(input).replaceAll("");
        resultado = PADRAO_JAVASCRIPT.matcher(resultado).replaceAll("");
        resultado = PADRAO_SQL_COMMENTS.matcher(resultado).replaceAll("");
        resultado = PADRAO_INJECAO_SQL.matcher(resultado).replaceAll("");
        resultado = PADRAO_PATH_TRAVERSAL.matcher(resultado).replaceAll("");
        resultado = PADRAO_CARACTERES_CONTROLE.matcher(resultado).replaceAll("");
        resultado = PADRAO_HTML_TAGS.matcher(resultado).replaceAll("");

        resultado = resultado.replaceAll("(?i)UNION\\s+SELECT", "");
        resultado = resultado.replaceAll("(?i)OR\\s+'1'\\s*=\\s*'1'", "");
        resultado = resultado.replaceAll("(?i)DROP\\s+TABLE", "");
        resultado = resultado.replaceAll("(?i)DELETE\\s+FROM", "");
        resultado = resultado.replaceAll("(?i)UPDATE\\s+.*\\s+SET", "");
        resultado = resultado.replaceAll("(?i)INSERT\\s+INTO", "");
        resultado = resultado.replaceAll("[<>&\"']", "");
        resultado = resultado.replaceAll("[@#$%&*()+={}\\[\\]|\\\\:;\"<>?/~`]", "");
        resultado = resultado.replaceAll("[\\t\\n\\r]", " ");
        resultado = resultado.replaceAll("\\s+", " ");
        resultado = resultado.trim();

        return resultado;
    }
}
