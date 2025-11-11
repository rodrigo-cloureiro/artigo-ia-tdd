package br.com.infnet.livraria_infnet.repository;

import br.com.infnet.livraria_infnet.model.Livro;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class LivroRepositoryImpl implements LivroRepository {
    private final List<Livro> livros;

    public LivroRepositoryImpl() {
        this.livros = initLivros();
    }

    @Override
    public Livro add(Livro novoLivro) {
        livros.add(novoLivro);
        return novoLivro;
    }

    @Override
    public List<Livro> findAll() {
        return Collections.unmodifiableList(livros);
    }

    @Override
    public Optional<Livro> findByIsbn(String isbn) {
        return livros.stream()
                .filter(livro -> livro.getIsbn().equals(isbn))
                .findFirst();
    }

    private List<Livro> initLivros() {
        List<Livro> temp = Collections.emptyList();
        ObjectMapper mapper = new ObjectMapper();

        try (InputStream is = getClass().getResourceAsStream("/data/livros.json")) {
            temp = mapper.readValue(is, new TypeReference<>() {
            });
            System.out.println("📚 " + temp.size() + " livros carregados com sucesso!");
        } catch (Exception e) {
            System.out.println("❌ Ocorreu um erro ao carregar os livros!");
        }

        return temp;
    }
}
