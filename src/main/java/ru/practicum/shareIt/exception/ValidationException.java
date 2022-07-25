package ru.practicum.shareIt.exception;

public class ValidationException extends RuntimeException {
   public ValidationException(final String message) {
        super(message);
    }
}