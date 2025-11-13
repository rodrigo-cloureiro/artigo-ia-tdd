package br.com.biblioteca.db;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.Objects;

/**
 * Gerencia conexões H2, executa schema e carrega fixtures CSV se existir.
 */
public class Database {

    private static final String JDBC_URL = "jdbc:h2:mem:biblioteca;DB_CLOSE_DELAY=-1;MODE=PostgreSQL";
    private static final String USER = "sa";
    private static final String PASS = "";

    private static Database instance;
    private Connection conn;

    private Database() {
        try {
            conn = DriverManager.getConnection(JDBC_URL, USER, PASS);
            runSchema();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static synchronized Database getInstance() {
        if (instance == null) instance = new Database();
        return instance;
    }

    public Connection getConnection() {
        return conn;
    }

    private void runSchema() {
        try (var in = getClass().getResourceAsStream("/schema.sql")) {
            Objects.requireNonNull(in, "schema.sql não encontrado");
            String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            try (Statement st = conn.createStatement()) {
                for (String stmt : sql.split(";")) {
                    if (stmt.trim().isEmpty()) continue;
                    st.execute(stmt);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao aplicar schema", e);
        }
    }

    /**
     * Carrega CSV de livros em /fixtures/books.csv (opcional) para popular o DB.
     * CSV: title,author,isbn
     */
    public void loadFixturesIfPresent() {
        try (var is = getClass().getResourceAsStream("/fixtures/books.csv")) {
            if (is == null) return;
            try (CSVParser parser = new CSVParser(new InputStreamReader(is, StandardCharsets.UTF_8), CSVFormat.DEFAULT.withHeader())) {
                String insert = "INSERT INTO books(title, author, isbn) VALUES(?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(insert)) {
                    for (CSVRecord r : parser) {
                        ps.setString(1, r.get("title").trim());
                        ps.setString(2, r.get("author").trim());
                        ps.setString(3, r.get("isbn").trim());
                        try {
                            ps.executeUpdate();
                        } catch (SQLException ex) {
                            // Ignore duplicates or bad lines
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
