package br.com.biblioteca;

import br.com.biblioteca.controller.BookController;
import br.com.biblioteca.repository.InMemoryBookRepository;
import br.com.biblioteca.service.BookService;
import br.com.biblioteca.model.Book;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class App {
    public static void main(String[] args) {
        InMemoryBookRepository repo = new InMemoryBookRepository();
        BookService service = new BookService(repo);
        BookController controller = new BookController(service);

        // Seed sample data
        try {
            repo.save(new Book(null, "Clean Code", "Robert C. Martin", "9780132350884"));
            repo.save(new Book(null, "Effective Java", "Joshua Bloch", "9780134685991"));
        } catch (Exception e) {
            // ignore if seeded incorrectly
        }

        Javalin app = Javalin.create(cfg -> {
            cfg.staticFiles.add("/static", Location.CLASSPATH);
            cfg.contextPath = "/";
        }).start(7000);

        app.get("/", controller.listView);
        app.get("/books/new", controller.showCreateForm);
        app.post("/books", controller.create);

        app.get("/books/:id", controller.view);
        app.get("/books/:id/edit", controller.showEditForm);
        app.post("/books/:id", controller.update);
        app.post("/books/:id/delete", controller.delete);

        // search endpoints via query params already handled in listView
        System.out.println("Aplicação iniciada em http://localhost:7000");
    }
}
