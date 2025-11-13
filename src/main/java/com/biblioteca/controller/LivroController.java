package com.biblioteca.controller;

import com.biblioteca.model.Livro;
import com.biblioteca.service.LivroService;
import com.biblioteca.security.SecurityUtils;
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
            // Sanitização das entradas
            String titulo = SecurityUtils.sanitizeInput(ctx.formParam("titulo"));
            String autor = SecurityUtils.sanitizeInput(ctx.formParam("autor"));
            String isbn = SecurityUtils.sanitizeInput(ctx.formParam("isbn"));

            Livro livro = new Livro(titulo, autor, isbn);

            livroService.cadastrarLivro(livro);
            ctx.redirect("/livros?sucesso=Livro+cadastrado+com+sucesso");

        } catch (IllegalArgumentException e) {
            Map<String, Object> model = new HashMap<>();
            model.put("livro", new Livro(
                    ctx.formParam("titulo"),
                    ctx.formParam("autor"),
                    ctx.formParam("isbn")
            ));
            model.put("erro", SecurityUtils.escapeHtml(e.getMessage()));
            model.put("tituloPagina", "Cadastrar Novo Livro");
            ctx.render("templates/livros/form.html", model);
        } catch (SecurityException e) {
            Map<String, Object> model = new HashMap<>();
            model.put("livro", new Livro());
            model.put("erro", "Entrada contém caracteres maliciosos. Por favor, use apenas texto simples.");
            model.put("tituloPagina", "Cadastrar Novo Livro");
            ctx.render("templates/livros/form.html", model);
        }
    }

    public void mostrarFormularioEdicao(Context ctx) {
        try {
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
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID inválido");
        }
    }

    public void atualizarLivro(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));

            // Sanitização das entradas
            String titulo = SecurityUtils.sanitizeInput(ctx.formParam("titulo"));
            String autor = SecurityUtils.sanitizeInput(ctx.formParam("autor"));
            String isbn = SecurityUtils.sanitizeInput(ctx.formParam("isbn"));

            Livro livroAtualizado = new Livro(titulo, autor, isbn);

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
            model.put("erro", SecurityUtils.escapeHtml(e.getMessage()));
            model.put("tituloPagina", "Editar Livro");
            ctx.render("templates/livros/form.html", model);
        } catch (SecurityException e) {
            Map<String, Object> model = new HashMap<>();
            model.put("livro", new Livro(
                    ctx.formParam("titulo"),
                    ctx.formParam("autor"),
                    ctx.formParam("isbn")
            ));
            model.put("erro", "Entrada contém caracteres maliciosos. Por favor, use apenas texto simples.");
            model.put("tituloPagina", "Editar Livro");
            ctx.render("templates/livros/form.html", model);
        }
    }

    public void deletarLivro(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));

            if (livroService.deletarLivro(id)) {
                ctx.redirect("/livros?sucesso=Livro+excluído+com+sucesso");
            } else {
                throw new NotFoundResponse("Livro não encontrado");
            }
        } catch (IllegalStateException e) {
            ctx.redirect("/livros?erro=" + SecurityUtils.escapeHtmlAttribute(e.getMessage().replace(" ", "+")));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID inválido");
        }
    }

    public void buscarLivros(Context ctx) {
        String tipo = ctx.queryParam("tipo");
        String termo = ctx.queryParam("termo");

        Map<String, Object> model = new HashMap<>();

        try {
            if (tipo != null && termo != null && !termo.trim().isEmpty()) {
                String termoSanitizado = SecurityUtils.sanitizeInput(termo);

                switch (tipo) {
                    case "titulo":
                        model.put("livros", livroService.buscarPorTitulo(termoSanitizado));
                        model.put("tituloPagina", "Busca por Título: " + SecurityUtils.escapeHtml(termoSanitizado));
                        break;
                    case "autor":
                        model.put("livros", livroService.buscarPorAutor(termoSanitizado));
                        model.put("tituloPagina", "Busca por Autor: " + SecurityUtils.escapeHtml(termoSanitizado));
                        break;
                    case "isbn":
                        Optional<Livro> livro = livroService.buscarPorIsbn(termoSanitizado);
                        model.put("livros", livro.map(List::of).orElse(List.of()));
                        model.put("tituloPagina", "Busca por ISBN: " + SecurityUtils.escapeHtml(termoSanitizado));
                        break;
                    default:
                        model.put("livros", livroService.listarTodos());
                        model.put("tituloPagina", "Todos os Livros");
                }
            } else {
                model.put("livros", livroService.listarTodos());
                model.put("tituloPagina", "Todos os Livros");
            }
        } catch (SecurityException e) {
            model.put("livros", List.of());
            model.put("tituloPagina", "Busca - Entrada Inválida");
            model.put("erroBusca", "Termo de busca contém caracteres maliciosos");
        }

        ctx.render("templates/livros/listar.html", model);
    }
}