package com.br.infnet.repository.implementations;

import com.br.infnet.model.Livro;
import com.br.infnet.repository.interfaces.iLivroRepository;
import com.br.infnet.security.SecurityConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LivroRepositoryImpl implements iLivroRepository {
    private final Map<Integer, Livro> acervo = new HashMap<>();
    private final AtomicInteger proximoId = new AtomicInteger(1);

    public LivroRepositoryImpl() {
        carregarLivrosDoCSV();
    }

    @Override
    public int gerarProximoId() {
        return proximoId.getAndIncrement();
    }

    @Override
    public void salvarLivro(Livro livro) {
        int novoId = gerarProximoId();
        Livro livroComId = new Livro(novoId, livro.getTitulo(), livro.getAutor(), livro.getIsbn());
        acervo.put(novoId, livroComId);
    }

    @Override
    public Livro buscarLivroPorId(int id) {
        return acervo.get(id);
    }

    @Override
    public Livro buscarLivroPorISBN(String isbn) {
        return acervo.values().stream()
                .filter(livro -> livro.getIsbn().equals(isbn))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Livro> listarLivros() {
        return new ArrayList<>(acervo.values());
    }

    @Override
    public List<Livro> listarLivrosPorTitulo(String titulo) {
        String tituloBusca = titulo.trim().toLowerCase();
        return acervo.values().stream()
                .filter(livro -> livro.getTitulo().toLowerCase().contains(tituloBusca))
                .toList();
    }

    @Override
    public List<Livro> listarLivrosPorAutor(String autor) {
        String autorBusca = autor.trim().toLowerCase();
        return acervo.values().stream()
                .filter(livro -> livro.getAutor().toLowerCase().contains(autorBusca))
                .toList();
    }

    @Override
    public boolean existeISBN(String isbn) {
        return acervo.values().stream()
                .anyMatch(livro -> livro.getIsbn().equals(isbn));
    }

    @Override
    public void atualizarLivro(Livro livro) {
        acervo.put(livro.getId(), livro);
    }

    @Override
    public void removerLivro(int id) {
        acervo.remove(id);
    }

    //Populando o app com os dados do CSV
    private void carregarLivrosDoCSV() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("test-data/livros-validos.csv")) {
            assert is != null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {

                String linha;
                boolean primeiraLinha = true;

                while ((linha = reader.readLine()) != null) {
                    if (primeiraLinha) {
                        primeiraLinha = false;
                        continue;
                    }

                    String[] dados = parseCsvLine(linha);
                    if (dados.length >= 3) {
                        try {
                            String titulo = processarCampoCSV(dados[0]);
                            String autor = processarCampoCSV(dados[1]);
                            String isbn = processarCampoCSV(dados[2]);
                            int livroId = gerarProximoId();

                            Livro livro = new Livro(livroId, titulo, autor, isbn);
                            acervo.put(livro.getId(), livro);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Livro ignorado do CSV (dados inválidos): " + e.getMessage());
                        }
                    }
                }

            }
        } catch (IOException | NullPointerException e) {
            System.out.println("Aviso: Não foi possível carregar livros do CSV: " + e.getMessage());
            System.out.println("Iniciando com acervo vazio.");
        }
    }

    private String[] parseCsvLine(String linha) {
        List<String> campos = new ArrayList<>();
        boolean dentroAspas = false;
        StringBuilder campoAtual = new StringBuilder();

        for (char c : linha.toCharArray()) {
            if (c == '"') {
                dentroAspas = !dentroAspas;
            } else if (c == ',' && !dentroAspas) {
                campos.add(campoAtual.toString());
                campoAtual = new StringBuilder();
            } else {
                campoAtual.append(c);
            }
        }
        campos.add(campoAtual.toString());

        return campos.toArray(new String[0]);
    }

    private String processarCampoCSV(String campo) {
        String campoLimpo = campo.replace("\"", "").trim();
        return SecurityConfig.processarEntrada(campoLimpo);
    }
}
