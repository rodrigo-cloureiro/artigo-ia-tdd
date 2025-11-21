package com.br.infnet.controller;

import com.br.infnet.model.Livro;
import com.br.infnet.repository.implementations.EmprestimoRepositoryImpl;
import com.br.infnet.repository.interfaces.iLivroRepository;
import com.br.infnet.service.EmprestimoService;
import com.br.infnet.service.LivroService;
import com.br.infnet.utils.FormValidator;
import com.br.infnet.utils.ErrorHandler;
import com.br.infnet.service.MultaPendenteException;
import com.br.infnet.view.EmprestimoView;
import io.javalin.Javalin;


public class EmprestimoController {
    private final EmprestimoService emprestimoService;

    public EmprestimoController(Javalin app, iLivroRepository livroRepository) {
        LivroService livroService = new LivroService(livroRepository);
        EmprestimoRepositoryImpl emprestimoRepository = new EmprestimoRepositoryImpl(livroService);
        this.emprestimoService = new EmprestimoService(emprestimoRepository, livroRepository);

        app.get("/emprestimos", ctx -> {
            try {
                ctx.html(EmprestimoView.renderEmprestimos(emprestimoService.listarEmprestimos(), emprestimoService));
            } catch (Exception e) {
                ctx.html(ErrorHandler.handleDatabaseError());
            }
        });

        app.get("/emprestimos/livros/{id}/emprestar", ctx -> {
            try {
                Integer idParam = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);
                if (idParam == null) {
                    ctx.html(ErrorHandler.handleValidationError("ID inválido"));
                    return;
                }

                Livro livro = emprestimoService.obterLivroPorId(idParam);
                if (livro == null) {
                    ctx.html(ErrorHandler.handleNotFound("livro"));
                    return;
                }

                if (!livro.isDisponivel()) {
                    ctx.html(ErrorHandler.handleBusinessLogicError("Livro não está disponível para empréstimo"));
                    return;
                }

                ctx.html(EmprestimoView.renderFormEmprestimo(livro));

            } catch (Exception e) {
                ctx.html(ErrorHandler.handleError(e));
            }
        });

        app.post("/emprestimos/livros/{id}/emprestar", ctx -> {
            try {
                Integer livroId = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);

                if (livroId == null) {
                    ctx.html(ErrorHandler.handleValidationError("ID do livro é inválido"));
                    return;
                }
                String prazoStr = ctx.formParam("prazo");


                // Validar prazo
                FormValidator.ValidationResult validation = FormValidator.validatePrazo(prazoStr);
                if (!validation.isValid()) {
                    Livro livro = emprestimoService.obterLivroPorId(livroId);
                    if (livro == null) {
                        ctx.html(ErrorHandler.handleNotFound("livro"));
                        return;
                    }
                    ctx.html(EmprestimoView.renderFormEmprestimo(livro, validation.getErrorMessage()));
                    return;
                }

                // Verificar se livro ainda existe e está disponível
                Livro livro = emprestimoService.obterLivroPorId(livroId);
                if (livro == null) {
                    ctx.html(ErrorHandler.handleNotFound("livro"));
                    return;
                }

                if (!livro.isDisponivel()) {
                    ctx.html(ErrorHandler.handleBusinessLogicError("Livro não está mais disponível para empréstimo"));
                    return;
                }

                assert prazoStr != null;
                int prazo = Integer.parseInt(prazoStr.trim());
                emprestimoService.emprestarLivro(livroId, prazo);
                ctx.redirect("/emprestimos");

            } catch (Exception e) {
                ctx.html(ErrorHandler.handleError(e));
            }
        });

        app.post("/emprestimos/livros/{id}/devolver", ctx -> {
            try {
                Integer idParam = ctx.pathParamAsClass("id", Integer.class).getOrDefault(null);
                if (idParam == null) {
                    ctx.html(ErrorHandler.handleValidationError("ID inválido"));
                    return;
                }

                emprestimoService.devolverLivro(idParam);
                ctx.redirect("/emprestimos");

            } catch (MultaPendenteException e) {
                ctx.html(EmprestimoView.renderMultaPendente(e.getMessage()));
            } catch (Exception e) {
                ctx.html(ErrorHandler.handleError(e));
            }
        });
    }
}