package com.gemini.jobcoin.models.api.response

import com.gemini.jobcoin.models.api.Transaction

data class AddressInfoResponse(
    val balance: String?,
    val transactions: List<Transaction>?
) {
    fun isNonZeroBalance(): Boolean {
        return !balance.isNullOrEmpty() &&
            (balance != "0")
    }
}
