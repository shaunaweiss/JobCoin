package com.gemini.jobcoin.models.mixer

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class MixerTransaction(
    val temporaryMixerAddress: String,
    val outgoingDepositAddresses: List<String>,
    var amount: String? = null
) {
    fun getNumDepositAddresses(): Int {
        return outgoingDepositAddresses.size
    }

    fun updateTransactionAmount(updatedAmount: String) {
        amount = updatedAmount
    }
}
