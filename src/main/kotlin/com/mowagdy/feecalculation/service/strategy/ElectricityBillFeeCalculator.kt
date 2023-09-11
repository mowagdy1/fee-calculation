package com.mowagdy.feecalculation.service.strategy

import java.math.BigDecimal

class ElectricityBillFeeCalculator : FeeCalculator {
    override fun calculateFee(txnAmount: BigDecimal): FeeCalculatorResult {
        val rate = BigDecimal("0.0013")
        val fee = txnAmount.multiply(rate)
        val description = "Electricity bill fee rate of 0.13%"

        return FeeCalculatorResult(fee, rate, description)
    }
}
