package com.br.infnet.utils;

import com.br.infnet.security.SecurityConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FormValidator {

    public record ValidationResult(boolean valid, List<String> errors) {
        public String getErrorMessage() {
            return errors.isEmpty() ? "" : String.join("; ", errors);
        }

        public boolean isValid() {
            return valid;
        }
    }

    public static ValidationResult validateLivro(Map<String, String> params) {
        List<String> errors = new ArrayList<>();

        String titulo = params.get("titulo");
        try {
            if (titulo != null) {
                titulo = SecurityConfig.processarEntrada(titulo);
                SecurityConfig.validarTitulo(titulo);
            } else {
                errors.add("Título é obrigatório");
            }
        } catch (IllegalArgumentException e) {
            errors.add("Título: " + e.getMessage());
        }

        String autor = params.get("autor");
        try {
            if (autor != null) {
                autor = SecurityConfig.processarEntrada(autor);
                SecurityConfig.validarAutor(autor);
            } else {
                errors.add("Autor é obrigatório");
            }
        } catch (IllegalArgumentException e) {
            errors.add("Autor: " + e.getMessage());
        }

        String isbn = params.get("isbn");
        try {
            if (isbn != null) {
                isbn = SecurityConfig.processarEntrada(isbn);
                SecurityConfig.validarIsbn(isbn);
            } else {
                errors.add("ISBN é obrigatório");
            }
        } catch (IllegalArgumentException e) {
            errors.add("ISBN: " + e.getMessage());
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult validatePrazo(String prazoStr) {
        List<String> errors = new ArrayList<>();

        if (prazoStr == null || prazoStr.trim().isEmpty()) {
            errors.add("Prazo é obrigatório");
        } else {
            try {
                prazoStr = SecurityConfig.processarEntrada(prazoStr);
                int prazo = Integer.parseInt(prazoStr.trim());

                if (prazo < 1) {
                    errors.add("Prazo deve ser pelo menos 1 dia");
                } else if (prazo > 365) {
                    errors.add("Prazo não pode exceder 1 ano");
                }
            } catch (NumberFormatException e) {
                errors.add("Prazo deve ser um número válido");
            } catch (IllegalArgumentException e) {
                errors.add("Prazo: " + e.getMessage());
            }
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    public static ValidationResult validateBusca(String tipo, String termo) {
        List<String> errors = new ArrayList<>();

        if (tipo == null || tipo.trim().isEmpty()) {
            errors.add("Tipo de busca é obrigatório");
        } else if (!List.of("titulo", "autor", "id").contains(tipo)) {
            errors.add("Tipo de busca inválido");
        }

        if (termo == null || termo.trim().isEmpty()) {
            errors.add("Termo de busca é obrigatório");
        } else {
            try {
                termo = SecurityConfig.processarEntrada(termo);

                if ("id".equals(tipo)) {
                    int id = Integer.parseInt(termo.trim());
                    if (id < 1) {
                        errors.add("ID deve ser um número positivo");
                    }
                }
            } catch (NumberFormatException e) {
                errors.add("ID deve ser um número válido");
            } catch (IllegalArgumentException e) {
                errors.add("Busca: " + e.getMessage());
            }
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }
}
