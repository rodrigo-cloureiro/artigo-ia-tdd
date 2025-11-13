package com.biblioteca.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private final List<String> errors;
    private final boolean isValid;

    private ValidationResult(boolean isValid, List<String> errors) {
        this.isValid = isValid;
        this.errors = errors;
    }

    public static ValidationResult valid() {
        return new ValidationResult(true, new ArrayList<>());
    }

    public static ValidationResult invalid(String error) {
        List<String> errors = new ArrayList<>();
        errors.add(error);
        return new ValidationResult(false, errors);
    }

    public static ValidationResult invalid(List<String> errors) {
        return new ValidationResult(false, new ArrayList<>(errors));
    }

    public boolean isValid() {
        return isValid;
    }

    public List<String> getErrors() {
        return new ArrayList<>(errors);
    }

    public String getErrorMessage() {
        return String.join(", ", errors);
    }

    public ValidationResult and(ValidationResult other) {
        if (this.isValid && other.isValid) {
            return valid();
        }

        List<String> allErrors = new ArrayList<>();
        if (!this.isValid) {
            allErrors.addAll(this.errors);
        }
        if (!other.isValid) {
            allErrors.addAll(other.errors);
        }

        return invalid(allErrors);
    }
}