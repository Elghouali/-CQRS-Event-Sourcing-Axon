package ma.enset.tp4.account.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletionException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handle(Exception exception, HttpServletRequest request) {
        Throwable cause = exception;
        while (cause.getCause() != null && (cause instanceof CompletionException ||
                cause instanceof java.util.concurrent.ExecutionException ||
                cause instanceof org.axonframework.commandhandling.CommandExecutionException ||
                cause instanceof org.axonframework.queryhandling.QueryExecutionException)) {
            cause = cause.getCause();
        }
        HttpStatus status;
        if (cause instanceof MethodArgumentNotValidException || cause instanceof IllegalArgumentException) {
            status = HttpStatus.BAD_REQUEST;
        } else if (cause instanceof NoSuchElementException ||
                cause instanceof org.axonframework.modelling.command.AggregateNotFoundException) {
            status = HttpStatus.NOT_FOUND;
        } else if (cause instanceof IllegalStateException) {
            status = HttpStatus.CONFLICT;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }
        String message = cause.getMessage() == null ? status.getReasonPhrase() : cause.getMessage();
        if (status == HttpStatus.INTERNAL_SERVER_ERROR) message = "Internal error (consult application logs)";
        return ResponseEntity.status(status).body(Map.of(
                "error", status.getReasonPhrase(), "message", message, "path", request.getRequestURI()));
    }
}
