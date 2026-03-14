package unaldi.bankservice.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import unaldi.bankservice.entity.dto.BankDTO;
import unaldi.bankservice.entity.request.BankSaveRequest;
import unaldi.bankservice.entity.request.BankUpdateRequest;
import unaldi.bankservice.service.abstracts.BankService;
import unaldi.bankservice.utils.result.DataResult;
import unaldi.bankservice.utils.result.Result;

import java.util.List;

import static unaldi.bankservice.security.SecurityUtil.isAdmin;

@RestController
@RequestMapping("api/v1/banks")
public class BankController {
    private final BankService bankService;

    @Autowired
    public BankController(BankService bankService) {
        this.bankService = bankService;
    }

    @PostMapping
    public ResponseEntity<DataResult<BankDTO>> save(
            @Valid @RequestBody BankSaveRequest bankSaveRequest,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.bankService.save(bankSaveRequest));
    }

    @PutMapping
    public ResponseEntity<DataResult<BankDTO>> update(
            @Valid @RequestBody BankUpdateRequest bankUpdateRequest,   // ✅ add @Valid
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.ok(this.bankService.update(bankUpdateRequest));
    }

    @DeleteMapping("/{bankId}")
    public ResponseEntity<Result> delete(
            @PathVariable Long bankId,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.ok(this.bankService.deleteById(bankId));
    }

    // Reads are allowed for both ADMIN and CUSTOMER
    @GetMapping("/{bankId}")
    public ResponseEntity<DataResult<BankDTO>> findById(@PathVariable Long bankId) {
        return ResponseEntity.ok(this.bankService.findById(bankId));
    }

    @GetMapping
    public ResponseEntity<DataResult<List<BankDTO>>> findAll() {
        return ResponseEntity.ok(this.bankService.findAll());
    }
}