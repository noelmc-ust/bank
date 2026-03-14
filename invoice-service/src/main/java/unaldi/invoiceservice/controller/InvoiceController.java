package unaldi.invoiceservice.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import unaldi.invoiceservice.entity.dto.InvoiceDTO;
import unaldi.invoiceservice.entity.request.InvoiceSaveRequest;
import unaldi.invoiceservice.entity.request.InvoiceUpdateRequest;
import unaldi.invoiceservice.service.abstracts.InvoiceService;
import unaldi.invoiceservice.utils.client.dto.UserResponse;
import unaldi.invoiceservice.utils.result.DataResult;
import unaldi.invoiceservice.utils.result.Result;

import java.util.List;

import static unaldi.invoiceservice.security.SecurityUtil.isAdmin;
import static unaldi.invoiceservice.security.SecurityUtil.isOwner;

@RestController
@RequestMapping("api/v1/invoices")
public class InvoiceController {
    private final InvoiceService invoiceService;

    @Autowired
    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // ========== Admin-only ==========

    @PostMapping
    public ResponseEntity<DataResult<InvoiceDTO>> save(
            @Valid @RequestBody InvoiceSaveRequest invoiceSaveRequest,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.invoiceService.save(invoiceSaveRequest));
    }

    @PutMapping
    public ResponseEntity<DataResult<InvoiceDTO>> update(
            @Valid @RequestBody InvoiceUpdateRequest invoiceUpdateRequest,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.ok(this.invoiceService.update(invoiceUpdateRequest));
    }

    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<Result> deleteById(
            @PathVariable Long invoiceId,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.ok(this.invoiceService.deleteById(invoiceId));
    }

    @GetMapping
    public ResponseEntity<DataResult<List<InvoiceDTO>>> findAll(
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.ok(this.invoiceService.findAll());
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<DataResult<InvoiceDTO>> findById(
            @PathVariable Long invoiceId,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        return ResponseEntity.ok(this.invoiceService.findById(invoiceId));
    }

    // ========== Customer-owned ==========

    // ✅ Used by your frontend: returns invoices for a user
    @GetMapping("/users/{userId}")
    public ResponseEntity<DataResult<List<InvoiceDTO>>> findByUserId(
            @PathVariable Long userId,
            @RequestHeader(value = "X-User-Id", required = false) String sub,
            @RequestHeader(value = "X-User-Roles", required = false) String rolesHeader
    ) {
        if (!isAdmin(rolesHeader) && !isOwner(sub, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
        var result = invoiceService.findByUserId(userId);
        return ResponseEntity.ok(result);
    }

    // Additional info endpoint (kept)
    @GetMapping("/users/{userId}/info")
    public ResponseEntity<DataResult<UserResponse>> findInvoiceUserByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(this.invoiceService.findInvoiceUserByUserId(userId));
    }
}