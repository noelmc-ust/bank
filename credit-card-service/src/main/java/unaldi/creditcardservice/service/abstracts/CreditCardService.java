package unaldi.creditcardservice.service.abstracts;

import unaldi.creditcardservice.entity.dto.CreditCardDTO;
import unaldi.creditcardservice.entity.request.CreditCardSaveRequest;
import unaldi.creditcardservice.entity.request.CreditCardUpdateRequest;
import unaldi.creditcardservice.utils.client.dto.BankResponse;
import unaldi.creditcardservice.utils.client.dto.UserResponse;
import unaldi.creditcardservice.utils.result.DataResult;
import unaldi.creditcardservice.utils.result.Result;

import java.util.List;

/**
 * Copyright (c) 2024
 * All rights reserved.
 */
public interface CreditCardService {
    DataResult<CreditCardDTO> save(CreditCardSaveRequest creditCardSaveRequest);
    DataResult<CreditCardDTO> update(CreditCardUpdateRequest creditCardUpdateRequest);
    Result deleteById(Long creditCardId);
    DataResult<CreditCardDTO> findById(Long creditCardId);
    DataResult<List<CreditCardDTO>> findAll();

    // Used by other services (kept)
    DataResult<UserResponse> findCreditCardUserByUserId(Long userId);
    DataResult<BankResponse> findCreditCardBankByBankId(Long bankId);

    // ✅ New: used by frontend “My Cards”
    DataResult<List<CreditCardDTO>> findByUserId(Long userId);
}