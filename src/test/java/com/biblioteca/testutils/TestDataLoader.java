package com.biblioteca.testutils;

import com.biblioteca.model.Livro;
import com.biblioteca.model.Emprestimo;
import com.biblioteca.repository.LivroRepository;
import com.biblioteca.repository.EmprestimoRepository;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestDataLoader {

    public static List<Livro> loadLivrosFromCSV() {
        List<Livro> livros = new ArrayList<>();

        try (InputStream inputStream = TestDataLoader.class.getClassLoader()
                .getResourceAsStream("test-data/livros.csv");
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Pular cabeçalho
                }

                String[] fields = line.split(",");
                if (fields.length >= 3) {
                    String titulo = fields[0].trim();
                    String autor = fields[1].trim();
                    String isbn = fields[2].trim();

                    Livro livro = new Livro(titulo, autor, isbn);
                    livros.add(livro);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar dados de teste", e);
        }

        return livros;
    }

    public static List<Emprestimo> loadEmprestimosFromCSV(LivroRepository livroRepository) {
        List<Emprestimo> emprestimos = new ArrayList<>();

        try (InputStream inputStream = TestDataLoader.class.getClassLoader()
                .getResourceAsStream("test-data/emprestimos.csv");
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Pular cabeçalho
                }

                String[] fields = line.split(",");
                if (fields.length >= 3) {
                    String isbnLivro = fields[0].trim();
                    String usuario = fields[1].trim();
                    int prazoDias = Integer.parseInt(fields[2].trim());

                    var livro = livroRepository.buscarPorIsbn(isbnLivro);
                    if (livro.isPresent()) {
                        Emprestimo emprestimo = new Emprestimo(livro.get(), usuario, prazoDias);

                        // Simular datas para testes
                        if (fields.length > 3) {
                            String dataDevolucao = fields[3].trim();
                            if (!dataDevolucao.isEmpty()) {
                                emprestimo.setDataDevolucaoReal(LocalDate.parse(dataDevolucao));
                                emprestimo.setDevolvido(true);
                            }
                        }

                        emprestimos.add(emprestimo);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar dados de empréstimos de teste", e);
        }

        return emprestimos;
    }

    public static void populateTestData(LivroRepository livroRepository,
                                        EmprestimoRepository emprestimoRepository) {
        // Limpar dados existentes
        livroRepository.limpar();
        emprestimoRepository.limpar();

        // Carregar livros
        List<Livro> livros = loadLivrosFromCSV();
        for (Livro livro : livros) {
            livroRepository.salvar(livro);
        }

        // Carregar empréstimos
        List<Emprestimo> emprestimos = loadEmprestimosFromCSV(livroRepository);
        for (Emprestimo emprestimo : emprestimos) {
            emprestimoRepository.salvar(emprestimo);
        }
    }
}