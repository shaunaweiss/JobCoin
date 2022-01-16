package com.gemini.jobcoin.models

data class Transaction(
    val timestamp: String?,
    val fromAddress: String?,
    val toAddress: String?,
    val amount: String?
)
