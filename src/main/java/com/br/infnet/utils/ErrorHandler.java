package com.br.infnet.utils;

import com.br.infnet.view.LivroView;

public class ErrorHandler {

    public static String handleError(Exception e) {
        String userMessage = getUserFriendlyMessage(e);
        String technicalMessage = shouldShowTechnicalDetails() ? e.getMessage() : null;

        System.err.println("Erro capturado: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        e.printStackTrace();

        return LivroView.renderError(userMessage, technicalMessage);
    }

    public static String handleValidationError(String validationMessage) {
        return LivroView.renderError("Dados inválidos", validationMessage);
    }

    public static String handleNotFound(String resourceType) {
        String message = switch (resourceType.toLowerCase()) {
            case "livro" -> "Livro não encontrado";
            case "emprestimo" -> "Empréstimo não encontrado";
            default -> "Recurso não encontrado";
        };
        return LivroView.renderError(message, "Verifique se o ID está correto e tente novamente");
    }

    public static String handleDatabaseError() {
        return LivroView.renderError(
                "Erro interno do sistema",
                "Não foi possível processar sua solicitação. Tente novamente em alguns minutos"
        );
    }

    public static String handleBusinessLogicError(String businessMessage) {
        return LivroView.renderError("Operação não permitida", businessMessage);
    }

    private static String getUserFriendlyMessage(Exception e) {
        return switch (e.getClass().getSimpleName()) {
            case "NumberFormatException" -> "Formato de número inválido";
            case "IllegalArgumentException" -> "Dados fornecidos são inválidos";
            case "SQLException" -> "Erro de acesso aos dados";
            case "IOException" -> "Erro de comunicação";
            default -> "Erro interno do sistema";
        };
    }

    private static boolean shouldShowTechnicalDetails() {
        return "development".equals(System.getProperty("environment", "production"));
    }
}
