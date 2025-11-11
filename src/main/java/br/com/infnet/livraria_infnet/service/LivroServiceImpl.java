package br.com.infnet.livraria_infnet.service;

import br.com.infnet.livraria_infnet.model.Livro;
import br.com.infnet.livraria_infnet.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LivroServiceImpl implements LivroService {
    private final LivroRepository livroRepository;

    @Override
    public Livro adicionar(Livro novoLivro) {
        return livroRepository.add(novoLivro);
    }

    @Override
    public List<Livro> listar() {
        return livroRepository.findAll()
                .stream()
                .filter(Livro::isAtivo)
                .toList();
    }

    @Override
    public Optional<Livro> buscarPorIsbn(String isbn) {
        return livroRepository.findByIsbn(cleanup(isbn));
    }

    @Override
    public void removerLivro(Livro livro) {
        livroRepository.remove(livro);
    }

    @Override
    public void removerLivroPorIsbn(String isbn) {
        livroRepository.removeByIsbn(cleanup(isbn));
    }

    private String cleanup(String isbn) {
        return isbn.replaceAll("[^0-9]", "");
    }
}
