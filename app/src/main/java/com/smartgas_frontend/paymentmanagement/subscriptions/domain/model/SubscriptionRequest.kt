package com.smartgas_frontend.paymentmanagement.subscriptions.domain.model

data class SubscriptionRequest(
    val accountId: Int,
    val planId: Int
)
