package com.gemini.jobcoin.models.api.response

import com.gemini.jobcoin.models.Transaction

data class AddressInfoResponse(
    val balance: String?,
    val transactions: List<Transaction>?
) : JobcoinResponse
