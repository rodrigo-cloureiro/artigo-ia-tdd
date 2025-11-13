package br.com.biblioteca.controller;

import br.com.biblioteca.model.Book;
import br.com.biblioteca.service.BookService;
import br.com.biblioteca.service.LoanService;
import br.com.biblioteca.service.exceptions.ValidationException;
import io.javalin.http.Handler;
import io.javalin.plugin.rendering.template.JavalinThymeleaf;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookController {

    private final BookService service;
    private final LoanService loanService;

    public BookController(BookService service, LoanService loanService) {
        this.service = service; this.loanService = loanService;
        setupThymeleaf();
    }

    private void setupThymeleaf() {
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("/templates/");
        resolver.setSuffix(".html");
        resolver.setCharacterEncoding("UTF-8");
        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(resolver);
        JavalinThymeleaf.configure(engine);
    }

    public Handler listView = ctx -> {
        String q = ctx.queryParam("q");
        String author = ctx.queryParam("author");
        String isbn = ctx.queryParam("isbn");
        Map<String,Object> model = new HashMap<>();
        if (isbn != null && !isbn.isBlank()) {
            service.findByIsbn(isbn).ifPresent(b -> model.put("books", List.of(b)));
            if (!model.containsKey("books")) model.put("books", List.of());
        } else if (q != null && !q.isBlank()) model.put("books", service.searchByTitle(q));
        else if (author != null && !author.isBlank()) model.put("books", service.searchByAuthor(author));
        else model.put("books", service.findAll());
        model.put("q", q); model.put("author", author); model.put("isbn", isbn);
        ctx.render("list", model);
    };

    public Handler showCreateForm = ctx -> {
        Map<String,Object> m = new HashMap<>(); m.put("book", new Book()); m.put("errors", List.of());
        ctx.render("form", m);
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
            Map<String,Object> m = new HashMap<>();
            m.put("errors", List.of(e.getMessage())); m.put("book", book);
            ctx.status(400).render("form", m);
        }
    };

    public Handler showEditForm = ctx -> {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Book book = service.findById(id);
        Map<String,Object> m = new HashMap<>(); m.put("book", book); m.put("errors", List.of());
        ctx.render("form", m);
    };

    public Handler update = ctx -> {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Book upd = new Book();
        upd.setTitle(ctx.formParam("title"));
        upd.setAuthor(ctx.formParam("author"));
        upd.setIsbn(ctx.formParam("isbn"));
        try {
            service.update(id, upd);
            ctx.redirect("/");
        } catch (ValidationException e) {
            Map<String,Object> m = new HashMap<>(); m.put("errors", List.of(e.getMessage())); m.put("book", upd);
            ctx.status(400).render("form", m);
        }
    };

    public Handler view = ctx -> {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Book book = service.findById(id);
        Map<String,Object> m = new HashMap<>(); m.put("book", book);
        ctx.render("view", m);
    };

    public Handler delete = ctx -> {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        if (loanService != null && loanService.isBookLoaned(id)) {
            ctx.status(400).result("Livro não pode ser excluído: encontra-se emprestado.");
            return;
        }
        service.delete(id);
        ctx.redirect("/");
    };
}
