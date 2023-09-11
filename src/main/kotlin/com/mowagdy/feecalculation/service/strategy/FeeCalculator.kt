package com.mowagdy.feecalculation.service.strategy

import java.math.BigDecimal

interface FeeCalculator {
    fun calculateFee(txnAmount: BigDecimal): FeeCalculatorResult
}

data class FeeCalculatorResult(
    val fee: BigDecimal,
    val rate: BigDecimal,
    val description: String
)
