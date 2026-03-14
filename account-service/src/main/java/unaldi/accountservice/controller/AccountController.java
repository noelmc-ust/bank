package unaldi.accountservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import unaldi.accountservice.entity.dto.AccountDTO;
import unaldi.accountservice.entity.request.AccountSaveRequest;
import unaldi.accountservice.entity.request.AccountUpdateRequest;
import unaldi.accountservice.service.abstracts.AccountService;
import unaldi.accountservice.utils.client.dto.BankResponse;
import unaldi.accountservice.utils.client.dto.UserResponse;
import unaldi.accountservice.utils.result.DataResult;
import unaldi.accountservice.utils.result.Result;

import java.util.List;

import static unaldi.accountservice.security.SecurityUtil.isAdmin;
import static unaldi.accountservice.security.SecurityUtil.isOwner;

/**
 * Copyright (c) 2024
 * All rights reserved.
 */
@RestController
@RequestMapping("api/v1/accounts")
public class AccountController {
    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // ========== Admin-only ==========

    @PostMapping
    public ResponseEntity<DataResult<AccountDTO>> save(
            (@Valid @RequestBody AccountSaveRequest accountSaveRequest,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.accountService.save(accountSaveRequest));
    }

    @PutMapping
    public ResponseEntity<DataResult<AccountDTO>> update(
            @RequestBody AccountUpdateRequest accountUpdateRequest,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.ok(this.accountService.update(accountUpdateRequest));
    }

    @DeleteMapping("/{accountId}")
    public ResponseEntity<Result> deleteById(
            @PathVariable Long accountId,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.ok(this.accountService.deleteById(accountId));
    }

    @GetMapping
    public ResponseEntity<DataResult<List<AccountDTO>>> findAll(
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.ok(this.accountService.findAll());
    }

    // Optional: admin-only for by-id (unless you implement account->user ownership check here)
    @GetMapping("/{accountId}")
    public ResponseEntity<DataResult<AccountDTO>> findById(
            @PathVariable Long accountId,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.ok(this.accountService.findById(accountId));
    }

    // ========== Customer-owned ==========

    // ✅ Used by frontend “My Accounts”
    @GetMapping("/users/{userId}")
    public ResponseEntity<DataResult<List<AccountDTO>>> findByUserId(
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Id", required = false) String sub,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader) && !isOwner(sub, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        return ResponseEntity.ok(accountService.findByUserId(userId));
    }

    // Keep info endpoints separate to avoid collision with the list endpoint above
    @GetMapping("/users/{userId}/info")
    public ResponseEntity<DataResult<UserResponse>> findAccountUserByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(this.accountService.findAccountUserByUserId(userId));
    }

    @GetMapping("/banks/{bankId}")
    public ResponseEntity<DataResult<BankResponse>> findAccountBankByBankId(@PathVariable Long bankId) {
        return ResponseEntity.ok(this.accountService.findAccountBankByBankId(bankId));
    }
}