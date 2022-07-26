package ru.practicum.shareIt.exception;

public class ConflictException extends RuntimeException {

    public ConflictException(final String message) {
        super(message);
    }
}
