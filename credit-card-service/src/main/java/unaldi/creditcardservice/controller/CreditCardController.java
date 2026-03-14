package unaldi.creditcardservice.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import unaldi.creditcardservice.entity.dto.CreditCardDTO;
import unaldi.creditcardservice.entity.request.CreditCardSaveRequest;
import unaldi.creditcardservice.entity.request.CreditCardUpdateRequest;
import unaldi.creditcardservice.service.abstracts.CreditCardService;
import unaldi.creditcardservice.utils.client.dto.BankResponse;
import unaldi.creditcardservice.utils.client.dto.UserResponse;
import unaldi.creditcardservice.utils.result.DataResult;
import unaldi.creditcardservice.utils.result.Result;

import java.util.List;

import static unaldi.creditcardservice.security.SecurityUtil.isAdmin;
import static unaldi.creditcardservice.security.SecurityUtil.isOwner;

@RestController
@RequestMapping("api/v1/creditCards")
public class CreditCardController {
    private final CreditCardService creditCardService;

    @Autowired
    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    // ========== Admin-only ==========

    @PostMapping
    public ResponseEntity<DataResult<CreditCardDTO>> save(
            @Valid @RequestBody CreditCardSaveRequest creditCardSaveRequest,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.creditCardService.save(creditCardSaveRequest));
    }

    @PutMapping
    public ResponseEntity<DataResult<CreditCardDTO>> update(
            @Valid @RequestBody CreditCardUpdateRequest creditCardUpdateRequest,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.ok(this.creditCardService.update(creditCardUpdateRequest));
    }

    @DeleteMapping("/{creditCardId}")
    public ResponseEntity<Result> deleteById(
            @PathVariable Long creditCardId,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.ok(this.creditCardService.deleteById(creditCardId));
    }

    @GetMapping
    public ResponseEntity<DataResult<List<CreditCardDTO>>> findAll(
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.ok(this.creditCardService.findAll());
    }

    // Optional: admin-only for id lookup (you can add ownership enforcement if needed)
    @GetMapping("/{creditCardId}")
    public ResponseEntity<DataResult<CreditCardDTO>> findById(
            @PathVariable Long creditCardId,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.ok(this.creditCardService.findById(creditCardId));
    }

    // ========== Customer-owned ==========

    // ✅ Used by the frontend: returns cards for a user
    @GetMapping("/users/{userId}")
    public ResponseEntity<DataResult<List<CreditCardDTO>>> findByUserId(
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Id", required = false) String sub,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader) && !isOwner(sub, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        var result = creditCardService.findByUserId(userId);
        return ResponseEntity.ok(result);
    }

    // Keep these info endpoints on different paths to avoid collision
    @GetMapping("/users/{userId}/info")
    public ResponseEntity<DataResult<UserResponse>> findCreditCardUserByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(this.creditCardService.findCreditCardUserByUserId(userId));
    }

    @GetMapping("/banks/{bankId}")
    public ResponseEntity<DataResult<BankResponse>> findCreditCardBankByBankId(@PathVariable Long bankId) {
        return ResponseEntity.ok(this.creditCardService.findCreditCardBankByBankId(bankId));
    }
}