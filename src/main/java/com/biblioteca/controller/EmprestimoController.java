package com.biblioteca.controller;

import com.biblioteca.model.Emprestimo;
import com.biblioteca.service.EmprestimoService;
import com.biblioteca.service.LivroService;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import java.util.HashMap;
import java.util.Map;

public class EmprestimoController {
    private final EmprestimoService emprestimoService;
    private final LivroService livroService;

    public EmprestimoController(EmprestimoService emprestimoService, LivroService livroService) {
        this.emprestimoService = emprestimoService;
        this.livroService = livroService;
    }

    public void listarEmprestimos(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("templates/emprestimos", emprestimoService.listarTodos());
        model.put("tituloPagina", "Todos os Empréstimos");
        ctx.render("templates/emprestimos/listar.html", model);
    }

    public void listarEmprestimosAtivos(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("templates/emprestimos", emprestimoService.listarAtivos());
        model.put("tituloPagina", "Empréstimos Ativos");
        ctx.render("templates/emprestimos/listar.html", model);
    }

    public void listarEmprestimosAtrasados(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("templates/emprestimos", emprestimoService.listarAtrasados());
        model.put("tituloPagina", "Empréstimos Atrasados");
        ctx.render("templates/emprestimos/listar.html", model);
    }

    public void mostrarFormularioEmprestimo(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("livros", livroService.listarTodos());
        model.put("tituloPagina", "Realizar Empréstimo");
        ctx.render("templates/emprestimos/form-emprestimo.html.html", model);
    }

    public void realizarEmprestimo(Context ctx) {
        try {
            Long livroId = Long.parseLong(ctx.formParam("livroId"));
            String usuario = ctx.formParam("usuario");
            int prazoDias = Integer.parseInt(ctx.formParam("prazoDias"));

            Emprestimo emprestimo = emprestimoService.realizarEmprestimo(livroId, usuario, prazoDias);

            ctx.redirect("/templates/emprestimos/ativos?sucesso=Empréstimo+realizado+com+sucesso");

        } catch (IllegalArgumentException e) {
            Map<String, Object> model = new HashMap<>();
            model.put("livros", livroService.listarTodos());
            model.put("erro", e.getMessage());
            model.put("tituloPagina", "Realizar Empréstimo");
            ctx.render("templates/emprestimos/form-emprestimo.html.html", model);
        }
    }

    public void mostrarDetalhesEmprestimo(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        var emprestimo = emprestimoService.buscarPorId(id);

        if (emprestimo.isPresent()) {
            Map<String, Object> model = new HashMap<>();
            model.put("emprestimo", emprestimo.get());
            model.put("tituloPagina", "Detalhes do Empréstimo");
            ctx.render("templates/emprestimos/detalhes.html", model);
        } else {
            throw new NotFoundResponse("Empréstimo não encontrado");
        }
    }

    public void registrarDevolucao(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));

        try {
            emprestimoService.registrarDevolucao(id);
            ctx.redirect("/templates/emprestimos");

        } catch (IllegalArgumentException | IllegalStateException e) {
            ctx.redirect("/templates/emprestimos/" + id + "?erro=" + e.getMessage().replace(" ", "+"));
        }
    }

    public void pagarMulta(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));

        try {
            emprestimoService.pagarMulta(id);
            ctx.redirect("/templates/emprestimos/" + id + "?sucesso=Multa+paga+com+sucesso");

        } catch (IllegalArgumentException e) {
            ctx.redirect("/templates/emprestimos/" + id + "?erro=" + e.getMessage().replace(" ", "+"));
        }
    }

    public void buscarEmprestimosPorUsuario(Context ctx) {
        String usuario = ctx.queryParam("usuario");

        Map<String, Object> model = new HashMap<>();

        if (usuario != null && !usuario.trim().isEmpty()) {
            model.put("templates/emprestimos", emprestimoService.buscarPorUsuario(usuario));
            model.put("tituloPagina", "Empréstimos do Usuário: " + usuario);
        } else {
            model.put("templates/emprestimos", emprestimoService.listarTodos());
            model.put("tituloPagina", "Todos os Empréstimos");
        }

        ctx.render("templates/emprestimos/listar.html", model);
    }
}