package com.biblioteca.controller;

import com.biblioteca.model.Livro;
import com.biblioteca.service.LivroService;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LivroController {
    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    public void listarLivros(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("livros", livroService.listarTodos());
        model.put("tituloPagina", "Todos os Livros");
        ctx.render("templates/livros/listar.html", model);
    }

    public void mostrarFormularioCadastro(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("livro", new Livro());
        model.put("tituloPagina", "Cadastrar Novo Livro");
        ctx.render("templates/livros/form.html", model);
    }

    public void cadastrarLivro(Context ctx) {
        try {
            Livro livro = new Livro(
                    ctx.formParam("titulo"),
                    ctx.formParam("autor"),
                    ctx.formParam("isbn")
            );

            livroService.cadastrarLivro(livro);
            ctx.redirect("/livros?sucesso=Livro+cadastrado+com+sucesso");

        } catch (IllegalArgumentException e) {
            Map<String, Object> model = new HashMap<>();
            model.put("livro", new Livro(
                    ctx.formParam("titulo"),
                    ctx.formParam("autor"),
                    ctx.formParam("isbn")
            ));
            model.put("erro", e.getMessage());
            model.put("tituloPagina", "Cadastrar Novo Livro");
            ctx.render("templates/livros/form.html", model);
        }
    }

    public void mostrarFormularioEdicao(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        Optional<Livro> livro = livroService.buscarPorId(id);

        if (livro.isPresent()) {
            Map<String, Object> model = new HashMap<>();
            model.put("livro", livro.get());
            model.put("tituloPagina", "Editar Livro");
            ctx.render("templates/livros/form.html", model);
        } else {
            throw new NotFoundResponse("Livro não encontrado");
        }
    }

    public void atualizarLivro(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));

        try {
            Livro livroAtualizado = new Livro(
                    ctx.formParam("titulo"),
                    ctx.formParam("autor"),
                    ctx.formParam("isbn")
            );

            Optional<Livro> livro = livroService.atualizarLivro(id, livroAtualizado);

            if (livro.isPresent()) {
                ctx.redirect("/livros?sucesso=Livro+atualizado+com+sucesso");
            } else {
                throw new NotFoundResponse("Livro não encontrado");
            }

        } catch (IllegalArgumentException e) {
            Map<String, Object> model = new HashMap<>();
            model.put("livro", new Livro(
                    ctx.formParam("titulo"),
                    ctx.formParam("autor"),
                    ctx.formParam("isbn")
            ));
            model.put("erro", e.getMessage());
            model.put("tituloPagina", "Editar Livro");
            ctx.render("templates/livros/form.html", model);
        }
    }

    public void deletarLivro(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));

        if (livroService.deletarLivro(id)) {
            ctx.redirect("/livros?sucesso=Livro+excluído+com+sucesso");
        } else {
            throw new NotFoundResponse("Livro não encontrado");
        }
    }

    public void buscarLivros(Context ctx) {
        String tipo = ctx.queryParam("tipo");
        String termo = ctx.queryParam("termo");

        Map<String, Object> model = new HashMap<>();

        if (tipo != null && termo != null && !termo.trim().isEmpty()) {
            switch (tipo) {
                case "titulo":
                    model.put("livros", livroService.buscarPorTitulo(termo));
                    model.put("tituloPagina", "Busca por Título: " + termo);
                    break;
                case "autor":
                    model.put("livros", livroService.buscarPorAutor(termo));
                    model.put("tituloPagina", "Busca por Autor: " + termo);
                    break;
                case "isbn":
                    Optional<Livro> livro = livroService.buscarPorIsbn(termo);
                    model.put("livros", livro.map(List::of).orElse(List.of()));
                    model.put("tituloPagina", "Busca por ISBN: " + termo);
                    break;
                default:
                    model.put("livros", livroService.listarTodos());
                    model.put("tituloPagina", "Todos os Livros");
            }
        } else {
            model.put("livros", livroService.listarTodos());
            model.put("tituloPagina", "Todos os Livros");
        }

        ctx.render("templates/livros/listar.html", model);
    }
}