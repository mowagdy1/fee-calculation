package com.mowagdy.feecalculation.dto

import com.mowagdy.feecalculation.domain.AssetType
import com.mowagdy.feecalculation.domain.TransactionState
import com.mowagdy.feecalculation.domain.TransactionType
import java.math.BigDecimal
import java.time.LocalDateTime

data class FeeCalculatingRequest(
    val transaction_id: String,
    val amount: BigDecimal,
    val asset: String,
    val asset_type: AssetType,
    val type: TransactionType,
    val state: TransactionState,
    val created_at: LocalDateTime
)
