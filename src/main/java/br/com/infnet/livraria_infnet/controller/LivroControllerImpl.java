package br.com.infnet.livraria_infnet.controller;

import br.com.infnet.livraria_infnet.model.Livro;
import br.com.infnet.livraria_infnet.service.LivroService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
public class LivroControllerImpl implements LivroController {
    private final LivroService livroService;

    @GetMapping("")
    public List<Livro> listar() {
        return livroService.listar();
    }

    @GetMapping("/{isbn}")
    public Optional<Livro> buscarPorIsbn(@PathVariable String isbn) {
        return livroService.buscarPorIsbn(isbn);
    }
}
