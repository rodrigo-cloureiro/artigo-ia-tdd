package br.com.infnet.livraria_infnet.service;

import br.com.infnet.livraria_infnet.model.Livro;
import br.com.infnet.livraria_infnet.repository.LivroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LivroServiceImpl implements LivroService {
    private final LivroRepository livroRepository;

    @Override
    public List<Livro> listar() {
        return livroRepository.findAll()
                .stream()
                .filter(Livro::isAtivo)
                .toList();
    }
}
