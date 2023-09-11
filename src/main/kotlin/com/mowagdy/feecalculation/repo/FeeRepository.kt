package com.mowagdy.feecalculation.repo

import com.mowagdy.feecalculation.domain.Fee
import org.springframework.data.jpa.repository.JpaRepository

interface FeeRepository : JpaRepository<Fee, Long>
