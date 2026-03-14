package unaldi.creditcardservice.utils.exception;

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
import unaldi.creditcardservice.utils.constant.ExceptionMessages;
import unaldi.creditcardservice.utils.exception.customExceptions.CreditCardNotFoundException;
import unaldi.creditcardservice.utils.exception.dto.ExceptionResponse;
import unaldi.creditcardservice.utils.rabbitMQ.enums.LogType;
import unaldi.creditcardservice.utils.rabbitMQ.enums.OperationType;
import unaldi.creditcardservice.utils.rabbitMQ.producer.LogProducer;
import unaldi.creditcardservice.utils.rabbitMQ.request.LogRequest;
import unaldi.creditcardservice.utils.result.DataResult;
import unaldi.creditcardservice.utils.result.ErrorDataResult;

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

    @ExceptionHandler(CreditCardNotFoundException.class)
    public ResponseEntity<DataResult<ExceptionResponse>> handleCreditCardNotFoundException(CreditCardNotFoundException exception, WebRequest request) {
        log.error("CreditCardNotFoundException occurred: {}", exception.getMessage(), exception);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorDataResult<>(
                        prepareExceptionResponse(exception, HttpStatus.NOT_FOUND, request),
                        ExceptionMessages.CREDIT_CARD_NOT_FOUND
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
                .serviceName("credit-card-service")
                .operationType(operationType)
                .logType(LogType.ERROR)
                .message(message)
                .timestamp(LocalDateTime.now())
                .exception(exception)
                .build();
    }
}