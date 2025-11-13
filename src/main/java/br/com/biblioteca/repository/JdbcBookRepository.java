package br.com.biblioteca.repository;

import br.com.biblioteca.db.Database;
import br.com.biblioteca.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcBookRepository implements BookRepository {

    private final Connection conn;

    public JdbcBookRepository() {
        this.conn = Database.getInstance().getConnection();
    }

    @Override
    public Book save(Book book) {
        try {
            if (book.getId() == null) {
                String insert = "INSERT INTO books(title, author, isbn) VALUES(?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, book.getTitle());
                    ps.setString(2, book.getAuthor());
                    ps.setString(3, book.getIsbn());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) book.setId(rs.getLong(1));
                    }
                }
            } else {
                String update = "UPDATE books SET title=?, author=?, isbn=? WHERE id=?";
                try (PreparedStatement ps = conn.prepareStatement(update)) {
                    ps.setString(1, book.getTitle());
                    ps.setString(2, book.getAuthor());
                    ps.setString(3, book.getIsbn());
                    ps.setLong(4, book.getId());
                    ps.executeUpdate();
                }
            }
            return book;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Book> findById(Long id) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id,title,author,isbn FROM books WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id,title,author,isbn FROM books WHERE isbn = ?")) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Book> findAll() {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id,title,author,isbn FROM books ORDER BY title")) {
            try (ResultSet rs = ps.executeQuery()) {
                List<Book> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Book> findByTitleContaining(String title) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id,title,author,isbn FROM books WHERE LOWER(title) LIKE ? ORDER BY title")) {
            ps.setString(1, "%" + title.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                List<Book> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Book> findByAuthorContaining(String author) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id,title,author,isbn FROM books WHERE LOWER(author) LIKE ? ORDER BY author")) {
            ps.setString(1, "%" + author.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                List<Book> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void deleteById(Long id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM books WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean existsByIsbn(String isbn) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM books WHERE isbn = ?")) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public boolean existsByIsbnAndNotId(String isbn, Long id) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT id FROM books WHERE isbn = ?")) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    long found = rs.getLong("id");
                    return found != id;
                }
                return false;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Book map(ResultSet rs) throws SQLException {
        return new Book(rs.getLong("id"), rs.getString("title"), rs.getString("author"), rs.getString("isbn"));
    }
}
