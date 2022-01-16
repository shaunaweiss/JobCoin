package com.gemini.jobcoin.models.api.request

data class JobcoinTransactionRequest(
    val fromAddress: String,
    val toAddress: String,
    val amount: String
)
