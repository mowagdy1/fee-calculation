package com.mowagdy.feecalculation.service.strategy

import java.math.BigDecimal

class MobileTopUpFeeCalculator : FeeCalculator {
    override fun calculateFee(txnAmount: BigDecimal): FeeCalculatorResult {
        val rate = BigDecimal("0.0015")
        val fee = txnAmount.multiply(rate).stripTrailingZeros()
        val description = "Standard fee rate of 0.15%"

        return FeeCalculatorResult(fee, rate, description)
    }
}
