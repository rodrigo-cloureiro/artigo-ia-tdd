package com.biblioteca.exception;

import com.biblioteca.validation.ValidationResult;
import io.javalin.http.Context;
import io.javalin.http.ExceptionHandler;
import io.javalin.http.HttpResponseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public static final ExceptionHandler<Exception> GENERAL_EXCEPTION_HANDLER = (e, ctx) -> {
        logger.error("Erro não tratado: {}", e.getMessage(), e);

        Map<String, Object> model = new HashMap<>();
        model.put("error", "Ocorreu um erro interno no servidor");
        model.put("message", "Tente novamente mais tarde");

        ctx.status(500);
        ctx.render("templates/error/error.html", model);
    };

    public static final ExceptionHandler<IllegalArgumentException> VALIDATION_EXCEPTION_HANDLER = (e, ctx) -> {
        logger.warn("Erro de validação: {}", e.getMessage());

        Map<String, Object> model = new HashMap<>();
        model.put("error", "Erro de validação");
        model.put("message", e.getMessage());

        ctx.status(400);

        String path = ctx.path();
        if (path.contains("/livros")) {
            ctx.render("templates/livros/form.html", model);
        } else if (path.contains("/emprestimos")) {
            ctx.render("templates/emprestimos/form-emprestimo.html", model);
        } else {
            ctx.render("templates/error/error.html", model);
        }
    };

    public static final ExceptionHandler<SecurityException> SECURITY_EXCEPTION_HANDLER = (e, ctx) -> {
        logger.warn("Tentativa de ataque detectada: {}", e.getMessage());

        Map<String, Object> model = new HashMap<>();
        model.put("error", "Entrada maliciosa detectada");
        model.put("message", "A entrada contém padrões suspeitos");

        ctx.status(400);
        ctx.render("templates/error/security-error.html", model);
    };

    public static final ExceptionHandler<HttpResponseException> HTTP_RESPONSE_EXCEPTION_HANDLER = (e, ctx) -> {
        logger.info("Erro HTTP: {} - {}", e.getStatus(), e.getMessage());

        Map<String, Object> model = new HashMap<>();
        model.put("error", "Erro " + e.getStatus());
        model.put("message", e.getMessage());

        ctx.status(e.getStatus());
        ctx.render("templates/error/error.html", model);
    };
}