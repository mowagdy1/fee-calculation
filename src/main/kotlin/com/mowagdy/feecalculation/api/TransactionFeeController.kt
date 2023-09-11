package com.mowagdy.feecalculation.api

import com.mowagdy.feecalculation.dto.FeeCalculatingRequest
import com.mowagdy.feecalculation.dto.FeeStatusUpdatingRequest
import com.mowagdy.feecalculation.dto.TransactionFeeResponse
import com.mowagdy.feecalculation.service.FeeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/transaction/fee")
class TransactionFeeController(private val feeService: FeeService) {

    @PostMapping
    fun calculate(@RequestBody request: FeeCalculatingRequest): ResponseEntity<TransactionFeeResponse> {
        val calculatedFee = feeService.calculate(request)
        return ResponseEntity.ok(calculatedFee)
    }

    @PostMapping("/{id}/charge")
    fun charge(@PathVariable id: Long): ResponseEntity<Void> {
        feeService.charge(id)
        return ResponseEntity.ok().build()
    }

    @PatchMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestBody request: FeeStatusUpdatingRequest
    ): ResponseEntity<Void> {
        feeService.updateStatus(id, request)
        return ResponseEntity.ok().build()
    }
}
