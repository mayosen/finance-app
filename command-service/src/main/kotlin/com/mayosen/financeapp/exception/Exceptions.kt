package com.mayosen.financeapp.exception

import java.math.BigDecimal

// TODO: Бросать специфичные исключения

open class BusinessException(
    message: String,
    throwable: Throwable? = null,
) : RuntimeException(message, throwable) {
    constructor(message: String) : this(message, null)
}

class UnsupportedCommandException(
    type: String,
) : BusinessException("Unsupported command type: $type")

class InsufficientFundsException(
    accountId: String,
    amount: BigDecimal,
) : BusinessException("Account $accountId has insufficient funds for amount $amount")

class EventProcessingException(
    message: String,
    cause: Throwable?,
) : BusinessException(message, cause)

class AccountNotFoundException(
    accountId: String,
) : BusinessException("Account not found: $accountId")
