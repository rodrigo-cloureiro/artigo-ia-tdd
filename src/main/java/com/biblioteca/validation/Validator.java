package com.biblioteca.validation;

import com.biblioteca.security.SecurityUtils;
import com.biblioteca.model.Livro;
import com.biblioteca.model.Emprestimo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Validator {

    public static ValidationResult validateLivro(Livro livro) {
        List<String> errors = new ArrayList<>();

        if (livro == null) {
            return ValidationResult.invalid("Livro não pode ser nulo");
        }

        // Validação de título
        if (!SecurityUtils.isValidTitle(livro.getTitulo())) {
            errors.add("Título inválido. Deve conter apenas letras, números e espaços (máx. 255 caracteres)");
        }

        // Validação de autor
        if (!SecurityUtils.isValidAuthor(livro.getAutor())) {
            errors.add("Autor inválido. Deve conter apenas letras e espaços (máx. 100 caracteres)");
        }

        // Validação de ISBN
        if (!SecurityUtils.isValidISBN(livro.getIsbn())) {
            errors.add("ISBN inválido. Deve ser um código ISBN-13 válido");
        }

        // Sanitização
        try {
            SecurityUtils.sanitizeInput(livro.getTitulo());
            SecurityUtils.sanitizeInput(livro.getAutor());
            SecurityUtils.sanitizeInput(livro.getIsbn());
        } catch (SecurityException e) {
            errors.add("Entrada contém caracteres ou padrões maliciosos: " + e.getMessage());
        }

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }

    public static ValidationResult validateEmprestimo(Emprestimo emprestimo) {
        List<String> errors = new ArrayList<>();

        if (emprestimo == null) {
            return ValidationResult.invalid("Empréstimo não pode ser nulo");
        }

        // Validação de usuário
        if (!SecurityUtils.isValidUsername(emprestimo.getUsuario())) {
            errors.add("Nome de usuário inválido. Use apenas letras, números, espaços e hífens (3-50 caracteres)");
        }

        // Validação de datas
        if (emprestimo.getDataEmprestimo() == null) {
            errors.add("Data de empréstimo não pode ser nula");
        } else if (emprestimo.getDataEmprestimo().isAfter(LocalDate.now())) {
            errors.add("Data de empréstimo não pode ser futura");
        }

        if (emprestimo.getDataDevolucaoPrevista() == null) {
            errors.add("Data de devolução prevista não pode ser nula");
        } else if (emprestimo.getDataDevolucaoPrevista().isBefore(emprestimo.getDataEmprestimo())) {
            errors.add("Data de devolução prevista não pode ser anterior à data de empréstimo");
        }

        // Sanitização
        try {
            SecurityUtils.sanitizeInput(emprestimo.getUsuario());
        } catch (SecurityException e) {
            errors.add("Nome de usuário contém caracteres maliciosos: " + e.getMessage());
        }

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }

    public static ValidationResult validateLoanParameters(Long livroId, String usuario, int prazoDias) {
        List<String> errors = new ArrayList<>();

        if (livroId == null || livroId <= 0) {
            errors.add("ID do livro inválido");
        }

        if (!SecurityUtils.isValidUsername(usuario)) {
            errors.add("Nome de usuário inválido");
        }

        if (!SecurityUtils.isValidLoanPeriod(prazoDias)) {
            errors.add("Prazo de empréstimo inválido. Deve ser entre 1 e 30 dias");
        }

        // Sanitização
        try {
            SecurityUtils.sanitizeInput(usuario);
        } catch (SecurityException e) {
            errors.add("Nome de usuário contém caracteres maliciosos");
        }

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.invalid(errors);
    }
}