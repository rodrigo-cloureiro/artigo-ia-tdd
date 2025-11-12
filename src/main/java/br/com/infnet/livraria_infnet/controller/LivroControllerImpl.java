package br.com.infnet.livraria_infnet.controller;

import br.com.infnet.livraria_infnet.model.Livro;
import br.com.infnet.livraria_infnet.service.LivroService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/livros")
@RequiredArgsConstructor
public class LivroControllerImpl implements LivroController {
    private final LivroService livroService;

    @Override
    @PostMapping("")
    public Livro adicionar(@RequestBody Livro livro) {
        return livroService.adicionar(livro);
    }

    @Override
    @GetMapping("")
    public List<Livro> listar() {
        return livroService.listar();
    }

    @Override
    @GetMapping("/{isbn}")
    public Optional<Livro> buscarPorIsbn(@PathVariable String isbn) {
        return livroService.buscarPorIsbn(isbn);
    }

    @Override
    @PutMapping("/{isbn}")
    public Livro atualizarLivro(@PathVariable String isbn, @RequestBody Livro livro) {
        return livroService.atualizarLivro(isbn, livro);
    }

    @Override
    @DeleteMapping("")
    public void removerLivro(@RequestBody Livro livro) {
        livroService.removerLivro(livro);
    }

    @Override
    @DeleteMapping("/{isbn}")
    public void removerLivroPorIsbn(@PathVariable String isbn) {
        livroService.removerLivroPorIsbn(isbn);
    }
}
