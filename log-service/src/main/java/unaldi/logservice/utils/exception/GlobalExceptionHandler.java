package unaldi.logservice.utils.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import unaldi.logservice.utils.constant.ExceptionMessages;
import unaldi.logservice.utils.exception.customExceptions.LogNotFoundException;
import unaldi.logservice.utils.exception.dto.ExceptionResponse;
import unaldi.logservice.utils.result.DataResult;
import unaldi.logservice.utils.result.ErrorDataResult;

import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(LogNotFoundException.class)
    public ResponseEntity<DataResult<ExceptionResponse>> handleLogNotFoundException(LogNotFoundException exception, WebRequest request) {
        log.error("LogNotFoundException occurred: {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorDataResult<>(
                        prepareExceptionResponse(exception, HttpStatus.NOT_FOUND, request),
                        ExceptionMessages.LOG_NOT_FOUND
                ));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<DataResult<ExceptionResponse>> handleResponseStatusException(
            ResponseStatusException exception, WebRequest request) {

        HttpStatus status = HttpStatus.resolve(exception.getStatusCode().value());
        if (status == null) status = HttpStatus.BAD_REQUEST;

        if (status == HttpStatus.FORBIDDEN) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ErrorDataResult<>(
                            prepareExceptionResponse(exception, HttpStatus.FORBIDDEN, request),
                            exception.getReason() != null ? exception.getReason() : "Forbidden"
                    ));
        }

        return ResponseEntity
                .status(status)
                .body(new ErrorDataResult<>(
                        prepareExceptionResponse(exception, status, request),
                        exception.getReason() != null ? exception.getReason() : status.getReasonPhrase()
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<DataResult<ExceptionResponse>> handleAccessDeniedException(
            AccessDeniedException exception, WebRequest request) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorDataResult<>(
                        prepareExceptionResponse(exception, HttpStatus.FORBIDDEN, request),
                        "Forbidden"
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<DataResult<ExceptionResponse>> handleAllException(Exception exception, WebRequest request) {
        log.error("Exception occurred: {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorDataResult<>(
                        prepareExceptionResponse(exception, HttpStatus.BAD_REQUEST, request),
                        ExceptionMessages.BAD_REQUEST
                ));
    }

    private ExceptionResponse prepareExceptionResponse(Exception exception, HttpStatus httpStatus, WebRequest request) {
        HttpServletRequest servletRequest = ((NativeWebRequest) request).getNativeRequest(HttpServletRequest.class);

        String httpMethod = Optional.ofNullable(servletRequest).map(HttpServletRequest::getMethod).orElse("Unknown");
        String requestPath = Optional.ofNullable(servletRequest).map(HttpServletRequest::getRequestURI).orElse("Unknown");

        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .httpStatus(httpStatus)
                .httpStatusCode(httpStatus.value())
                .httpMethod(httpMethod)
                .errorType(exception.getClass().getSimpleName())
                .requestPath(requestPath)
                .build();
    }
}