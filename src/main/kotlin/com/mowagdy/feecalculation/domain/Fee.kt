package com.mowagdy.feecalculation.domain

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
class Fee(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val txnId: String,

    @Column(nullable = false)
    val txnAmount: BigDecimal,

    @Column(nullable = false)
    val txnAsset: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val txnAssetType: AssetType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val txnType: TransactionType,

    @Column(nullable = false)
    val fee: BigDecimal,

    @Column(nullable = false)
    val rate: BigDecimal,

    @Column(nullable = false)
    val description: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: FeeStatus,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class AssetType {
    FIAT,
    DIGITAL
}

enum class TransactionType {
    MobileTopUp,
    ElectricityBill
}

enum class TransactionState {
    CREATED,
    PROCESSING,
    SETTLED,
    FAILED
}

enum class FeeStatus {
    PENDING,
    CHARGE_INITIATED,
    RETRY,
    CHARGED,
    FAILED,
}
