package br.com.biblioteca.model;

import java.time.LocalDate;
import java.util.Objects;

public class Loan {
    private Long id;
    private Long bookId;
    private String borrower; // nome do tomador
    private LocalDate loanDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // quando devolvido
    private boolean finePaid;

    public Loan() {}

    public Loan(Long id, Long bookId, String borrower, LocalDate loanDate, LocalDate dueDate) {
        this.id = id;
        this.bookId = bookId;
        this.borrower = borrower;
        this.loanDate = loanDate;
        this.dueDate = dueDate;
        this.finePaid = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public String getBorrower() { return borrower; }
    public void setBorrower(String borrower) { this.borrower = borrower; }

    public LocalDate getLoanDate() { return loanDate; }
    public void setLoanDate(LocalDate loanDate) { this.loanDate = loanDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public boolean isFinePaid() { return finePaid; }
    public void setFinePaid(boolean finePaid) { this.finePaid = finePaid; }

    public boolean isReturned() { return this.returnDate != null; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Loan)) return false;
        Loan loan = (Loan) o;
        return Objects.equals(id, loan.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
