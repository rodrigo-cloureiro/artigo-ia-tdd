package com.biblioteca.exception;

public class CustomExceptions {

    public static class LivroNotFoundException extends RuntimeException {
        public LivroNotFoundException(String message) {
            super(message);
        }
    }

    public static class EmprestimoNotFoundException extends RuntimeException {
        public EmprestimoNotFoundException(String message) {
            super(message);
        }
    }

    public static class BusinessException extends RuntimeException {
        public BusinessException(String message) {
            super(message);
        }
    }

    public static class SecurityViolationException extends RuntimeException {
        public SecurityViolationException(String message) {
            super(message);
        }
    }
}