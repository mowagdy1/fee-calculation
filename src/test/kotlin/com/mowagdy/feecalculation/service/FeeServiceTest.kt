package com.mowagdy.feecalculation.service

import com.mowagdy.feecalculation.domain.*
import com.mowagdy.feecalculation.dto.FeeCalculatingRequest
import com.mowagdy.feecalculation.dto.FeeStatusUpdatingRequest
import com.mowagdy.feecalculation.repo.FeeRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.BDDMockito.*
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockitoExtension::class)
class FeeServiceTest {

    @Mock
    lateinit var feeRepository: FeeRepository

    @Test
    fun `Given a transaction, when calculate is called, then it should return the correct fee`() {
        // Given
        val service = FeeService(feeRepository)
        val request = FeeCalculatingRequest(
            transaction_id = "txn_001",
            amount = BigDecimal(1000),
            asset = "USD",
            asset_type = AssetType.FIAT,
            type = TransactionType.MobileTopUp,
            state = TransactionState.SETTLED,
            created_at =  LocalDateTime.parse("2023-08-30T15:42:17.610059")
        )

        given(feeRepository.save(any())).willAnswer { it.getArgument(0) }

        // When
        val response = service.calculate(request)

        // Then
        assertEquals(BigDecimal("1.5"), response.fee)
        assertEquals(BigDecimal("0.0015"), response.rate)
        assertEquals("Standard fee rate of 0.15%", response.description)
    }

    @Test
    fun `Given a fee with pending status, when updateStatus is called with CHARGE_INITIATED, then it should update the status`() {
        // Given
        val service = FeeService(feeRepository)
        val fee = Fee(
            txnId = "123",
            txnAmount = BigDecimal.TEN,
            txnAsset = "USD",
            txnAssetType = AssetType.FIAT,
            txnType = TransactionType.MobileTopUp,
            fee = BigDecimal("0.015"),
            rate = BigDecimal("0.0015"),
            description = "Standard fee rate of 0.15%",
            status = FeeStatus.PENDING
        )
        val request = FeeStatusUpdatingRequest(FeeStatus.CHARGE_INITIATED)

        given(feeRepository.findById(1L)).willReturn(Optional.of(fee))
        given(feeRepository.save(any())).willAnswer { it.getArgument(0) }

        // When
        service.updateStatus(1L, request)

        // Then
        assertEquals(FeeStatus.CHARGE_INITIATED, fee.status)
    }

    @Test
    fun `Given a fee with charged status, when updateStatus is called with RETRY, then it should throw an exception`() {
        // Given
        val service = FeeService(feeRepository)
        val fee = Fee(
            txnId = "123",
            txnAmount = BigDecimal.TEN,
            txnAsset = "USD",
            txnAssetType = AssetType.FIAT,
            txnType = TransactionType.MobileTopUp,
            fee = BigDecimal("0.015"),
            rate = BigDecimal("0.0015"),
            description = "Standard fee rate of 0.15%",
            status = FeeStatus.CHARGED
        )
        val request = FeeStatusUpdatingRequest(FeeStatus.RETRY)

        given(feeRepository.findById(1L)).willReturn(Optional.of(fee))

        // When
        val exception = assertThrows(IllegalStateException::class.java) {
            service.updateStatus(1L, request)
        }

        // Then
        assertEquals("Invalid status transition from CHARGED to RETRY", exception.message)
    }
}
