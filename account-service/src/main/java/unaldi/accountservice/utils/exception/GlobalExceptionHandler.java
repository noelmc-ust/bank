package unaldi.accountservice.utils.exception;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import unaldi.accountservice.utils.constant.ExceptionMessages;
import unaldi.accountservice.utils.exception.customExceptions.AccountNotFoundException;
import unaldi.accountservice.utils.exception.dto.ExceptionResponse;
import unaldi.accountservice.utils.rabbitMQ.enums.LogType;
import unaldi.accountservice.utils.rabbitMQ.enums.OperationType;
import unaldi.accountservice.utils.rabbitMQ.producer.LogProducer;
import unaldi.accountservice.utils.rabbitMQ.request.LogRequest;
import unaldi.accountservice.utils.result.DataResult;
import unaldi.accountservice.utils.result.ErrorDataResult;

import java.time.LocalDateTime;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private final LogProducer logProducer;

    @Autowired
    public GlobalExceptionHandler(LogProducer logProducer) {
        this.logProducer = logProducer;
    }

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<DataResult<ExceptionResponse>> handleAccountNotFoundException(AccountNotFoundException exception, WebRequest request) {
        log.error("AccountNotFoundException occurred: {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorDataResult<>(
                        prepareExceptionResponse(exception, HttpStatus.NOT_FOUND, request),
                        ExceptionMessages.ACCOUNT_NOT_FOUND
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

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<DataResult<ExceptionResponse>> handleFeignNotFoundException(FeignException.NotFound exception, WebRequest request) {
        log.error("Feign NotFoundException occurred: {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorDataResult<>(
                        prepareExceptionResponse(exception, HttpStatus.NOT_FOUND, request),
                        ExceptionMessages.RESOURCE_NOT_FOUND
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
        String exceptionMessage = httpStatus + " - " + exception.getClass().getSimpleName();

        logProducer.sendToLog(prepareLogRequest(OperationType.valueOf(httpMethod), exception.getMessage(), exceptionMessage));

        return ExceptionResponse.builder()
                .message(exception.getMessage())
                .httpStatus(httpStatus)
                .httpStatusCode(httpStatus.value())
                .httpMethod(httpMethod)
                .errorType(exception.getClass().getSimpleName())
                .requestPath(requestPath)
                .build();
    }

    private LogRequest prepareLogRequest(OperationType operationType, String message, String exception) {
        return LogRequest.builder()
                .serviceName("account-service")
                .operationType(operationType)
                .logType(LogType.ERROR)
                .message(message)
                .timestamp(LocalDateTime.now())
                .exception(exception)
                .build();
    }
}