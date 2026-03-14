package unaldi.logservice.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import unaldi.logservice.model.enums.LogType;
import unaldi.logservice.model.enums.OperationType;

import java.time.LocalDateTime;

public record LogSaveRequest(
        @NotBlank String serviceName,
        @NotNull OperationType operationType,
        @NotNull LogType logType,
        @NotBlank String message,
        @NotNull LocalDateTime timestamp,
        String exception
) {}