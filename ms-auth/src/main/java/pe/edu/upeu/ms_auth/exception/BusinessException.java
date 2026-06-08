package pe.edu.upeu.ms_auth.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
    private final HttpStatus status = HttpStatus.CONFLICT;

    public BusinessException(String message) {
        super(message);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
