package com.br.infnet.service;

import com.br.infnet.model.Livro;
import com.br.infnet.security.SecurityConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LivroService {
    private final AtomicInteger contadorId = new AtomicInteger(1);
    private final Map<Integer, Livro> acervo = new HashMap<>();

    public int gerarId() {
        return contadorId.getAndIncrement();
    }

    public LivroService() {
        carregarLivrosDoCSV();
    }

    //Populando o app com os dados do CSV
    protected void carregarLivrosDoCSV() {
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

                            Livro livro = new Livro(contadorId.getAndIncrement(), titulo, autor, isbn);
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

    public boolean existeISBN(String isbn) {
        return acervo.values().stream()
                .anyMatch(livro -> livro.getIsbn().equals(isbn));
    }

    public void cadastrarLivroNoAcervo(Livro livro) {
        validarLivro(livro);

        if (existeISBN(livro.getIsbn())) {
            throw new IllegalArgumentException("Já existe um livro cadastrado com este ISBN");
        }

        acervo.put(livro.getId(), livro);
    }

    public Livro buscarLivroPorIDNoAcervo(int id) {
        if (!acervo.containsKey(id)) {
            throw new NoSuchElementException("Livro não encontrado");
        }
        return acervo.get(id);
    }

    public ArrayList<Livro> buscarLivroPorTituloNoAcervo(String titulo) {
        validarTermoBusca(titulo, "Título");

        String tituloBusca = SecurityConfig.processarEntrada(titulo.trim().toLowerCase());
        ArrayList<Livro> livrosDeMesmoNome = new ArrayList<>();

        for (Livro livro : acervo.values()) {
            if (livro.getTitulo() != null &&
                    livro.getTitulo().toLowerCase().contains(tituloBusca)) {
                livrosDeMesmoNome.add(livro);
            }
        }
        return livrosDeMesmoNome;
    }

    public Livro buscarLivroPorISBN(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return null;
        }

        String isbnBusca = SecurityConfig.processarEntrada(isbn);
        return acervo.values().stream()
                .filter(livro -> livro.getIsbn().equals(isbnBusca))
                .findFirst()
                .orElse(null);
    }

    public ArrayList<Livro> buscarLivroPorAutorNoAcervo(String autor) {
        validarTermoBusca(autor, "Autor");

        String autorBusca = SecurityConfig.processarEntrada(autor.trim().toLowerCase());
        ArrayList<Livro> livrosDoAutor = new ArrayList<>();

        for (Livro livro : acervo.values()) {
            if (livro.getAutor() != null &&
                    livro.getAutor().toLowerCase().contains(autorBusca)) {
                livrosDoAutor.add(livro);
            }
        }
        return livrosDoAutor;
    }

    public void atualizarLivroDoAcervo(int id, String titulo, String autor, String isbn) {
        Livro livro = acervo.get(id);

        if (livro == null) {
            throw new NoSuchElementException("Livro não encontrado");
        }
        String tituloProcessado = SecurityConfig.processarEntrada(titulo);
        String autorProcessado = SecurityConfig.processarEntrada(autor);
        String isbnProcessado = SecurityConfig.processarEntrada(isbn);

        Livro livroComISBN = buscarLivroPorISBN(isbnProcessado);
        if (livroComISBN != null && livroComISBN.getId() != id) {
            throw new IllegalArgumentException("Já existe um livro cadastrado com este ISBN");
        }

        livro.setTitulo(tituloProcessado);
        livro.setAutor(autorProcessado);
        livro.setIsbn(isbnProcessado);
    }

    public void removerLivroDoAcervo(int id) {
        Livro livro = acervo.get(id);

        if (livro == null) {
            throw new NoSuchElementException("Livro não encontrado");
        }

        if (!livro.isDisponivel()) {
            throw new IllegalStateException("Livro está emprestado e não pode ser removido do acervo");
        }
        acervo.remove(id);
    }

    public ArrayList<Livro> listarLivrosDoAcervo() {
        return new ArrayList<>(acervo.values());
    }

    public void emprestarLivro(int id, int prazoDevolucao) {
        validarPrazoEmprestimo(prazoDevolucao);

        Livro livro = acervo.get(id);
        if (livro == null) {
            throw new NoSuchElementException("Livro não encontrado");
        }

        if (!livro.isDisponivel()) {
            throw new IllegalStateException("Livro já está emprestado");
        }

        livro.setDataEmprestimo(LocalDate.now());
        livro.setPrazoDevolucao(prazoDevolucao);
        livro.setDataEstimadaDevolucao(livro.getDataEmprestimo().plusDays(prazoDevolucao));
        livro.setDisponivel(false);
    }

    public void devolverLivro(int id) throws MultaPendenteException {
        Livro livro = acervo.get(id);

        if (livro == null) {
            throw new NoSuchElementException("Livro não encontrado");
        }

        if (livro.isDisponivel()) {
            throw new IllegalStateException("Livro não está emprestado");
        }

        double multa = calcularMulta(id);

        if (multa > 0) {
            livro.setMulta(multa);
            throw new MultaPendenteException("Pendente pagamento de multa no valor de R$ " + String.format("%.2f", multa));
        }

        livro.setDataEfetivaDevolucao(LocalDate.now());
        livro.setMulta(0);
        livro.setDisponivel(true);
        livro.setPrazoDevolucao(0);
        livro.setDataEstimadaDevolucao(null);
        livro.setDataEfetivaDevolucao(null);
    }

    public double calcularMulta(int livroId) {
        Livro livro = acervo.get(livroId);
        if (livro == null) {
            throw new NoSuchElementException("Livro não encontrado");
        }
        int diasAtraso = calcularDiasAtraso(livro);
        return CalculadoraMulta.calcular(diasAtraso);
    }

    private int calcularDiasAtraso(Livro livro) {
        LocalDate dataEmprestimo = livro.getDataEmprestimo();
        LocalDate dataEfetivaDevolucao = livro.getDataEfetivaDevolucao();

        if (dataEfetivaDevolucao == null) {
            dataEfetivaDevolucao = LocalDate.now();
        }
        int diasDecorridos = dataEmprestimo.until(dataEfetivaDevolucao).getDays();

        //Multa só se aplica se passou dos 10 dias gratuitos
        if (diasDecorridos <= 10) {
            return 0;
        }
        return diasDecorridos - 10;
    }

    public ArrayList<Livro> listarLivrosEmEmprestimo() {
        ArrayList<Livro> livrosEmprestados = new ArrayList<>();
        for (Livro livro : acervo.values()) {
            if (!livro.isDisponivel()) {
                livrosEmprestados.add(livro);
            }
        }
        return livrosEmprestados;
    }

    private void validarLivro(Livro livro) {
        if (livro == null) {
            throw new IllegalArgumentException("Livro não pode ser nulo");
        }

        SecurityConfig.validarTitulo(livro.getTitulo());
        SecurityConfig.validarAutor(livro.getAutor());
        SecurityConfig.validarIsbn(livro.getIsbn());
    }

    private void validarTermoBusca(String termo, String campo) {
        if (termo == null || termo.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " inválido");
        }
    }

    private void validarPrazoEmprestimo(int prazo) {
        if (prazo <= 0) {
            throw new IllegalArgumentException("Prazo de devolução deve ser positivo");
        }
        if (prazo > 365) {
            throw new IllegalArgumentException("Prazo de devolução não pode exceder 365 dias");
        }
    }
}
