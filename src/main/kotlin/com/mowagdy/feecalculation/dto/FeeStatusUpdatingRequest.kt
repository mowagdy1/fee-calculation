package com.mowagdy.feecalculation.dto

import com.mowagdy.feecalculation.domain.FeeStatus

data class FeeStatusUpdatingRequest(
    val status: FeeStatus,
)
