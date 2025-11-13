package br.com.biblioteca.util;

import br.com.biblioteca.model.Book;
import br.com.biblioteca.dao.BookDAO;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Utilitário para carregar dados iniciais de um arquivo CSV.
 */
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    private static final String CSV_FILE = "data.csv";

    /**
     * Carrega dados do CSV para o sistema.
     * @param bookDAO O DAO para persistir os livros.
     */
    public static void loadInitialData(BookDAO bookDAO) {
        try (CSVReader reader = new CSVReader(new InputStreamReader(
                DataLoader.class.getClassLoader().getResourceAsStream(CSV_FILE)
        ))) {
            List<String[]> records = reader.readAll();

            // Pula a linha do cabeçalho
            if (records.size() > 1) {
                for (int i = 1; i < records.size(); i++) {
                    String[] record = records.get(i);
                    // O ID do CSV é ignorado, a DAO gera o ID
                    String title = record[1];
                    String author = record[2];
                    String isbn = record[3];

                    Book newBook = new Book(title, author, isbn);
                    bookDAO.save(newBook);
                    logger.info("Livro carregado do CSV: {}", title);
                }
            }
        } catch (IOException | CsvException e) {
            logger.error("Falha ao carregar dados iniciais do CSV: {}", e.getMessage());
        }
    }
}