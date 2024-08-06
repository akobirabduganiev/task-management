package tech.nuqta.taskmanagement.exception;

public class AppConflictException extends RuntimeException {
    public AppConflictException(String message) {
        super(message);
    }
}
