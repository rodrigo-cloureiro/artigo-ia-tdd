package br.com.infnet.livraria_infnet.controller;

import br.com.infnet.livraria_infnet.model.Livro;
import br.com.infnet.livraria_infnet.service.LivroService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/livros")
@RequiredArgsConstructor
public class LivroControllerImpl implements LivroController {
    private final LivroService livroService;

    @GetMapping("/")
    public List<Livro> listar() {
        return livroService.listar();
    }
}
