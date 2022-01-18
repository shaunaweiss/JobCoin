package com.gemini.jobcoin.models.api.response

import com.gemini.jobcoin.models.api.Transaction

data class AddressInfoResponse(
    val balance: String = "",
    val transactions: List<Transaction>?
) {
    private fun getBalanceAsDouble(): Double? {
        return balance.toDoubleOrNull()
    }

    fun isNonZeroBalance(): Boolean {
        return balance.isNotEmpty() &&
            ( getBalanceAsDouble()!! > 0.0)
    }
}
