package br.com.biblioteca.util;

/**
 * Sanitiza entradas e fornece escape básico para evitar XSS.
 */
public final class InputSanitizer {

    private InputSanitizer() {}

    public static String sanitize(String input) {
        if (input == null) return null;
        String step = input.replaceAll("(?i)<script.*?>.*?</script>", "")
                .replace("\u0000", "")
                .trim();
        // simples escape
        return step.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}
