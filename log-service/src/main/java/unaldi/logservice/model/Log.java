package unaldi.logservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import unaldi.logservice.model.enums.LogType;
import unaldi.logservice.model.enums.OperationType;

import java.time.LocalDateTime;

@Document(collection = "logs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Log {
    @Id
    private String id;

    @Indexed
    @Field(name = "service_name")
    private String serviceName;

    @Field("operation_type")
    private OperationType operationType;

    @Indexed
    @Field("log_type")
    private LogType logType;

    @Field(name = "message")
    private String message;

    @Indexed
    @Field(name = "timestamp")
    private LocalDateTime timestamp;

    @Field(name = "exception")
    private String exception;
}