package com.mowagdy.feecalculation.service

import com.mowagdy.feecalculation.domain.Fee
import com.mowagdy.feecalculation.domain.FeeStatus
import com.mowagdy.feecalculation.domain.TransactionType
import com.mowagdy.feecalculation.dto.FeeCalculatingRequest
import com.mowagdy.feecalculation.dto.FeeStatusUpdatingRequest
import com.mowagdy.feecalculation.dto.TransactionFeeResponse
import com.mowagdy.feecalculation.repo.FeeRepository
import com.mowagdy.feecalculation.service.strategy.ElectricityBillFeeCalculator
import com.mowagdy.feecalculation.service.strategy.FeeCalculator
import com.mowagdy.feecalculation.service.strategy.MobileTopUpFeeCalculator
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FeeService(private val feeRepository: FeeRepository) {

    private val calculators: Map<TransactionType, FeeCalculator> = mapOf(
        TransactionType.MobileTopUp to MobileTopUpFeeCalculator(),
        TransactionType.ElectricityBill to ElectricityBillFeeCalculator()
    )

    @Transactional
    fun calculate(request: FeeCalculatingRequest): TransactionFeeResponse {

        val calculator = calculators[request.type] ?: throw IllegalArgumentException("Unsupported transaction type")
        val result = calculator.calculateFee(request.amount)

        var fee = Fee(
            txnId = request.transaction_id,
            txnAmount = request.amount,
            txnAsset = request.asset,
            txnAssetType = request.asset_type,
            txnType = request.type,
            fee = result.fee,
            rate = result.rate,
            description = result.description,
            status = FeeStatus.PENDING
        )

        fee = feeRepository.save(fee)

        return TransactionFeeResponse(fee)
    }


    @Transactional
    fun charge(feeId: Long) {

        val fee = feeRepository.findById(feeId).orElseThrow()
        fee.status = FeeStatus.CHARGE_INITIATED
        feeRepository.save(fee)

        // Here => Call the payment service to charge the customer..
    }


    @Transactional
    fun updateStatus(id: Long, request: FeeStatusUpdatingRequest) {
        val fee = feeRepository.findById(id).orElseThrow { IllegalArgumentException("Invalid fee ID") }

        val newStatus = request.status

        if (!isValidTransition(fee.status, newStatus)) {
            throw IllegalStateException("Invalid status transition from ${fee.status} to $newStatus")
        }

        fee.status = newStatus

        feeRepository.save(fee)
    }

    private fun isValidTransition(oldStatus: FeeStatus, newStatus: FeeStatus): Boolean {
        return when (oldStatus) {
            FeeStatus.PENDING -> newStatus in listOf(FeeStatus.CHARGE_INITIATED)
            FeeStatus.CHARGE_INITIATED -> newStatus in listOf(FeeStatus.CHARGED, FeeStatus.RETRY)
            FeeStatus.RETRY -> newStatus in listOf(FeeStatus.CHARGED, FeeStatus.FAILED)
            FeeStatus.CHARGED -> false
            FeeStatus.FAILED -> newStatus == FeeStatus.RETRY
        }
    }
}
