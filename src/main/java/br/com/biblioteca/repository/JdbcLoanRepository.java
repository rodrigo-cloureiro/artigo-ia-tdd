package br.com.biblioteca.repository;

import br.com.biblioteca.db.Database;
import br.com.biblioteca.model.Loan;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcLoanRepository implements LoanRepository {

    private final Connection conn;

    public JdbcLoanRepository() {
        this.conn = Database.getInstance().getConnection();
    }

    @Override
    public Loan save(Loan loan) {
        try {
            if (loan.getId() == null) {
                String insert = "INSERT INTO loans(book_id, borrower, loan_date, due_date, return_date, fine_paid) VALUES(?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1, loan.getBookId());
                    ps.setString(2, loan.getBorrower());
                    ps.setDate(3, Date.valueOf(loan.getLoanDate()));
                    ps.setDate(4, Date.valueOf(loan.getDueDate()));
                    if (loan.getReturnDate() != null) ps.setDate(5, Date.valueOf(loan.getReturnDate()));
                    else ps.setNull(5, Types.DATE);
                    ps.setBoolean(6, loan.isFinePaid());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) loan.setId(rs.getLong(1));
                    }
                }
            } else {
                String update = "UPDATE loans SET book_id=?, borrower=?, loan_date=?, due_date=?, return_date=?, fine_paid=? WHERE id=?";
                try (PreparedStatement ps = conn.prepareStatement(update)) {
                    ps.setLong(1, loan.getBookId());
                    ps.setString(2, loan.getBorrower());
                    ps.setDate(3, Date.valueOf(loan.getLoanDate()));
                    ps.setDate(4, Date.valueOf(loan.getDueDate()));
                    if (loan.getReturnDate() != null) ps.setDate(5, Date.valueOf(loan.getReturnDate()));
                    else ps.setNull(5, Types.DATE);
                    ps.setBoolean(6, loan.isFinePaid());
                    ps.setLong(7, loan.getId());
                    ps.executeUpdate();
                }
            }
            return loan;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Loan> findById(Long id) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM loans WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
            return Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Loan> findAll() {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM loans ORDER BY loan_date DESC")) {
            try (ResultSet rs = ps.executeQuery()) {
                List<Loan> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Loan> findActiveLoans() {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM loans WHERE return_date IS NULL")) {
            try (ResultSet rs = ps.executeQuery()) {
                List<Loan> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Loan> findActiveByBookId(Long bookId) {
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM loans WHERE book_id = ? AND return_date IS NULL")) {
            ps.setLong(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
            return Optional.empty();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void deleteById(Long id) {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM loans WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    private Loan map(ResultSet rs) throws SQLException {
        Loan l = new Loan();
        l.setId(rs.getLong("id"));
        l.setBookId(rs.getLong("book_id"));
        l.setBorrower(rs.getString("borrower"));
        l.setLoanDate(rs.getDate("loan_date").toLocalDate());
        l.setDueDate(rs.getDate("due_date").toLocalDate());
        Date rd = rs.getDate("return_date");
        if (rd != null) l.setReturnDate(rd.toLocalDate());
        l.setFinePaid(rs.getBoolean("fine_paid"));
        return l;
    }
}
