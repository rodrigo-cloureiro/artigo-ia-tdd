package br.com.biblioteca.controller;

import br.com.biblioteca.model.Book;
import br.com.biblioteca.service.BookService;
import br.com.biblioteca.service.exceptions.BookNotFoundException;
import br.com.biblioteca.service.exceptions.ValidationException;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookController {

    private final BookService service;

    public BookController(BookService service) {
        this.service = service;
        setupThymeleaf();
    }

    private void setupThymeleaf() {
        // Configure Thymeleaf template resolver
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateMode("HTML");
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        JavalinThymeleaf.configure(engine);
    }

    public Handler listView = ctx -> {
        String query = ctx.queryParam("q");
        String author = ctx.queryParam("author");
        String isbn = ctx.queryParam("isbn");
        Map<String, Object> model = new HashMap<>();
        if (isbn != null && !isbn.isBlank()) {
            service.findByIsbn(isbn).ifPresent(b -> model.put("books", List.of(b)));
            if (!model.containsKey("books")) model.put("books", List.of());
        } else if (query != null && !query.isBlank()) {
            model.put("books", service.searchByTitle(query));
        } else if (author != null && !author.isBlank()) {
            model.put("books", service.searchByAuthor(author));
        } else {
            model.put("books", service.findAll());
        }
        model.put("q", query);
        model.put("author", author);
        model.put("isbn", isbn);
        ctx.render("list", model);
    };

    public Handler showCreateForm = ctx -> {
        Map<String, Object> model = new HashMap<>();
        model.put("book", new Book());
        model.put("errors", List.of());
        ctx.render("form", model);
    };

    public Handler create = ctx -> {
        Book book = new Book();
        book.setTitle(ctx.formParam("title"));
        book.setAuthor(ctx.formParam("author"));
        book.setIsbn(ctx.formParam("isbn"));
        try {
            service.create(book);
            ctx.redirect("/");
        } catch (ValidationException e) {
            Map<String, Object> model = new HashMap<>();
            model.put("book", book);
            model.put("errors", List.of(e.getMessage()));
            ctx.render("form", model);
        }
    };

    public Handler showEditForm = ctx -> {
        Long id = Long.valueOf(ctx.pathParam("id"));
        try {
            Book book = service.findById(id);
            Map<String, Object> model = new HashMap<>();
            model.put("book", book);
            model.put("errors", List.of());
            ctx.render("form", model);
        } catch (BookNotFoundException e) {
            ctx.status(404).result("Livro não encontrado");
        }
    };

    public Handler update = ctx -> {
        Long id = Long.valueOf(ctx.pathParam("id"));
        Book update = new Book();
        update.setTitle(ctx.formParam("title"));
        update.setAuthor(ctx.formParam("author"));
        update.setIsbn(ctx.formParam("isbn"));
        try {
            service.update(id, update);
            ctx.redirect("/");
        } catch (ValidationException e) {
            Map<String, Object> model = new HashMap<>();
            update.setId(id);
            model.put("book", update);
            model.put("errors", List.of(e.getMessage()));
            ctx.render("form", model);
        } catch (BookNotFoundException e) {
            ctx.status(404).result("Livro não encontrado");
        }
    };

    public Handler view = ctx -> {
        Long id = Long.valueOf(ctx.pathParam("id"));
        try {
            Book book = service.findById(id);
            Map<String, Object> model = new HashMap<>();
            model.put("book", book);
            ctx.render("view", model);
        } catch (BookNotFoundException e) {
            ctx.status(404).result("Livro não encontrado");
        }
    };

    public Handler delete = ctx -> {
        Long id = Long.valueOf(ctx.pathParam("id"));
        try {
            service.delete(id);
            ctx.redirect("/");
        } catch (BookNotFoundException e) {
            ctx.status(404).result("Livro não encontrado");
        }
    };
}
