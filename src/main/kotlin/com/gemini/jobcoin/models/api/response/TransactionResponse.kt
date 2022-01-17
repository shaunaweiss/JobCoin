package com.gemini.jobcoin.models.api.response

import com.gemini.jobcoin.models.Transaction

data class TransactionResponse(
    val transactions: List<Transaction>
)
