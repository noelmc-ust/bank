package unaldi.logservice.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import unaldi.logservice.model.dto.LogDTO;
import unaldi.logservice.model.request.LogSaveRequest;
import unaldi.logservice.model.request.LogUpdateRequest;
import unaldi.logservice.service.abstracts.LogService;
import unaldi.logservice.utils.result.DataResult;
import unaldi.logservice.utils.result.Result;

import java.util.List;

import static unaldi.logservice.security.SecurityUtil.isAdmin;

@RestController
@RequestMapping("api/v1/logs")
public class LogController {
    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @PostMapping
    public ResponseEntity<DataResult<LogDTO>> save(
            @Valid @RequestBody LogSaveRequest logSaveRequest,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.logService.save(logSaveRequest));
    }

    @PutMapping
    public ResponseEntity<DataResult<LogDTO>> update(
            @Valid @RequestBody LogUpdateRequest logUpdateRequest,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.ok(this.logService.update(logUpdateRequest));
    }

    @DeleteMapping("/{logId}")
    public ResponseEntity<Result> deleteById(
            @PathVariable String logId,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.ok(this.logService.deleteById(logId));
    }

    @GetMapping("/{logId}")
    public ResponseEntity<DataResult<LogDTO>> findById(
            @PathVariable String logId,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.ok(this.logService.findById(logId));
    }

    @GetMapping
    public ResponseEntity<DataResult<List<LogDTO>>> findAll(
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.ok(this.logService.findAll());
    }
}