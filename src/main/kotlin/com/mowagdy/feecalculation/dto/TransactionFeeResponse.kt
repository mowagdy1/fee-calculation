package com.mowagdy.feecalculation.dto

import com.mowagdy.feecalculation.domain.Fee
import com.mowagdy.feecalculation.domain.TransactionType
import java.math.BigDecimal

data class TransactionFeeResponse(
    val id: Long?,
    val transaction_id: String,
    val amount: BigDecimal,
    val asset: String,
    val type: TransactionType,
    val fee: BigDecimal,
    val rate: BigDecimal,
    val description: String
) {
    constructor(fee: Fee) : this(
        fee.id,
        fee.txnId,
        fee.txnAmount,
        fee.txnAsset,
        fee.txnType,
        fee.fee,
        fee.rate,
        fee.description
    )
}
