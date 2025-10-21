package com.br.infnet.controller;

import com.br.infnet.model.Livro;
import com.br.infnet.service.LivroService;
import com.br.infnet.utils.FormValidator;
import com.br.infnet.utils.ErrorHandler;
import com.br.infnet.service.MultaPendenteException;
import com.br.infnet.view.LivroView;
import io.javalin.Javalin;

import java.util.HashMap;
import java.util.Map;

public class LivroController {
    private final LivroService service = new LivroService();

    public LivroController(Javalin app) {
        app.get("/", ctx -> ctx.redirect("/livros"));

        //********************Rotas para CRUD biblioteca**************************
        app.get("/livros", ctx -> {
            try {
                ctx.html(LivroView.renderList(service.listarLivrosDoAcervo()));
            } catch (Exception e) {
                ctx.html(ErrorHandler.handleDatabaseError());
            }
        });

        app.get("/livros/novo", ctx -> {
            try {
                ctx.html(LivroView.renderForm(new HashMap<>()));
            } catch (Exception e) {
                ctx.html(ErrorHandler.handleError(e));
            }
        });

        app.post("/livros", ctx -> {
            try {
                Map<String, String> params = new HashMap<>();
                params.put("titulo", ctx.formParam("titulo"));
                params.put("autor", ctx.formParam("autor"));
                params.put("isbn", ctx.formParam("isbn"));

                // Validar dados
                FormValidator.ValidationResult validation = FormValidator.validateLivro(params);
                if (!validation.isValid()) {
                    Map<String, Object> model = new HashMap<>(params);
                    model.put("erro", validation.getErrorMessage());
                    ctx.html(LivroView.renderForm(model));
                    return;
                }

                // Sanitizar dados
                String titulo = params.get("titulo").trim();
                String autor = params.get("autor").trim();
                String isbn = params.get("isbn").replaceAll("[^0-9]", "");

                // Verificar se ISBN já existe
                if (service.existeISBN(isbn)) {
                    Map<String, Object> model = new HashMap<>(params);
                    model.put("erro", "ISBN já cadastrado no sistema");
                    ctx.html(LivroView.renderForm(model));
                    return;
                }

                // Criar livro
                int id = service.gerarId();
                Livro livro = new Livro(id, titulo, autor, isbn);
                service.cadastrarLivroNoAcervo(livro);
                ctx.redirect("/livros");

            } catch (Exception e) {
                ctx.html(ErrorHandler.handleError(e));
            }
        });

        app.get("/livros/{id}/editar", ctx -> {
            try {
                Integer idParam = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);
                if (idParam == null) {
                    ctx.html(ErrorHandler.handleValidationError("ID inválido"));
                    return;
                }

                Livro livro = service.buscarLivroPorIDNoAcervo(idParam);
                if (livro == null) {
                    ctx.html(ErrorHandler.handleNotFound("livro"));
                    return;
                }

                Map<String, Object> model = new HashMap<>();
                model.put("id", livro.getId());
                model.put("titulo", livro.getTitulo());
                model.put("autor", livro.getAutor());
                model.put("isbn", livro.getIsbn());
                ctx.html(LivroView.renderForm(model));

            } catch (Exception e) {
                ctx.html(ErrorHandler.handleError(e));
            }
        });

        app.post("/livros/{id}/editar", ctx -> {
            try {
                Integer idParam = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);
                if (idParam == null) {
                    ctx.html(ErrorHandler.handleValidationError("ID inválido"));
                    return;
                }

                Map<String, String> params = new HashMap<>();
                params.put("titulo", ctx.formParam("titulo"));
                params.put("autor", ctx.formParam("autor"));
                params.put("isbn", ctx.formParam("isbn"));

                // Validar dados
                FormValidator.ValidationResult validation = FormValidator.validateLivro(params);
                if (!validation.isValid()) {
                    Map<String, Object> model = new HashMap<>(params);
                    model.put("erro", validation.getErrorMessage());
                    model.put("id", idParam);
                    ctx.html(LivroView.renderForm(model));
                    return;
                }

                // Sanitizar dados
                String titulo = params.get("titulo").trim();
                String autor = params.get("autor").trim();
                String isbn = params.get("isbn").replaceAll("[^0-9]", "");

                // Verificar se ISBN já existe em outro livro
                Livro livroComISBN = service.buscarLivroPorISBN(isbn);
                if (livroComISBN != null && livroComISBN.getId() != idParam) {
                    Map<String, Object> model = new HashMap<>(params);
                    model.put("erro", "ISBN já cadastrado em outro livro");
                    model.put("id", idParam);
                    ctx.html(LivroView.renderForm(model));
                    return;
                }

                service.atualizarLivroDoAcervo(idParam, titulo, autor, isbn);
                ctx.redirect("/livros");

            } catch (Exception e) {
                ctx.html(ErrorHandler.handleError(e));
            }
        });

        app.post("/livros/{id}/remover", ctx -> {
            try {
                Integer idParam = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);
                if (idParam == null) {
                    ctx.html(ErrorHandler.handleValidationError("ID inválido"));
                    return;
                }

                Livro livro = service.buscarLivroPorIDNoAcervo(idParam);
                if (livro == null) {
                    ctx.html(ErrorHandler.handleNotFound("livro"));
                    return;
                }

                if (!livro.isDisponivel()) {
                    ctx.html(ErrorHandler.handleBusinessLogicError("Não é possível remover um livro que está emprestado"));
                    return;
                }

                service.removerLivroDoAcervo(idParam);
                ctx.redirect("/livros");

            } catch (Exception e) {
                ctx.html(ErrorHandler.handleError(e));
            }
        });

        //********************Rotas para empréstimos**************************
        app.get("/emprestimos", ctx -> {
            try {
                ctx.html(LivroView.renderEmprestimos(service.listarLivrosEmEmprestimo()));
            } catch (Exception e) {
                ctx.html(ErrorHandler.handleDatabaseError());
            }
        });

        app.get("/livros/{id}/emprestar", ctx -> {
            try {
                Integer idParam = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);
                if (idParam == null) {
                    ctx.html(ErrorHandler.handleValidationError("ID inválido"));
                    return;
                }

                Livro livro = service.buscarLivroPorIDNoAcervo(idParam);
                if (livro == null) {
                    ctx.html(ErrorHandler.handleNotFound("livro"));
                    return;
                }

                if (!livro.isDisponivel()) {
                    ctx.html(ErrorHandler.handleBusinessLogicError("Livro não está disponível para empréstimo"));
                    return;
                }

                ctx.html(LivroView.renderFormEmprestimo(livro));

            } catch (Exception e) {
                ctx.html(ErrorHandler.handleError(e));
            }
        });

        app.post("/livros/{id}/emprestar", ctx -> {
            try {
                Integer idParam = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);
                if (idParam == null) {
                    ctx.html(ErrorHandler.handleValidationError("ID inválido"));
                    return;
                }

                String prazoStr = ctx.formParam("prazo");

                // Validar prazo
                FormValidator.ValidationResult validation = FormValidator.validatePrazo(prazoStr);
                if (!validation.isValid()) {
                    Livro livro = service.buscarLivroPorIDNoAcervo(idParam);
                    if (livro == null) {
                        ctx.html(ErrorHandler.handleNotFound("livro"));
                        return;
                    }

                    Map<String, Object> model = new HashMap<>();
                    model.put("livro", livro);
                    model.put("erro", validation.getErrorMessage());
                    ctx.html(LivroView.renderFormEmprestimo(livro, validation.getErrorMessage()));
                    return;
                }

                // Verificar se livro ainda existe e está disponível
                Livro livro = service.buscarLivroPorIDNoAcervo(idParam);
                if (livro == null) {
                    ctx.html(ErrorHandler.handleNotFound("livro"));
                    return;
                }

                if (!livro.isDisponivel()) {
                    ctx.html(ErrorHandler.handleBusinessLogicError("Livro não está mais disponível para empréstimo"));
                    return;
                }

                int prazo = Integer.parseInt(prazoStr.trim());
                service.emprestarLivro(idParam, prazo);
                ctx.redirect("/emprestimos");

            } catch (Exception e) {
                ctx.html(ErrorHandler.handleError(e));
            }
        });

        app.post("/livros/{id}/devolver", ctx -> {
            try {
                Integer idParam = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);
                if (idParam == null) {
                    ctx.html(ErrorHandler.handleValidationError("ID inválido"));
                    return;
                }

                service.devolverLivro(idParam);
                ctx.redirect("/emprestimos");

            } catch (MultaPendenteException e) {
                ctx.html(LivroView.renderMultaPendente(e.getMessage()));
            } catch (Exception e) {
                ctx.html(ErrorHandler.handleError(e));
            }
        });

        app.get("/buscar", ctx -> {
            try {
                String tipo = ctx.queryParam("tipo");
                String termo = ctx.queryParam("termo");

                if (tipo != null && !tipo.trim().isEmpty() &&
                        termo != null && !termo.trim().isEmpty()) {

                    //validação de busca
                    FormValidator.ValidationResult validation = FormValidator.validateBusca(tipo, termo);
                    if (!validation.isValid()) {
                        ctx.html(ErrorHandler.handleValidationError(validation.getErrorMessage()));
                        return;
                    }
                }

                ctx.html(LivroView.renderBusca(tipo, termo, service));

            } catch (Exception e) {
                ctx.html(ErrorHandler.handleError(e));
            }
        });
    }
}