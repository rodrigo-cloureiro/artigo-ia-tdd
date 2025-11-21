package com.br.infnet.app;

import com.br.infnet.controller.EmprestimoController;
import com.br.infnet.controller.LivroController;
import com.br.infnet.repository.implementations.LivroRepositoryImpl;
import com.br.infnet.repository.interfaces.iLivroRepository;
import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create();
        iLivroRepository livroRepository = new LivroRepositoryImpl();
        new LivroController(app, livroRepository);
        new EmprestimoController(app, livroRepository);
        app.start(7000);
    }
}