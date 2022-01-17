package com.gemini.jobcoin.models.mixer

data class MixerTransaction(
    val temporaryMixerAddress: String, // refer to this as a temporary address
    // val transactionId: Int,
    val outgoingDepositAddresses: List<String>,
    var amount: String? = null
) {
    fun updateTransactionAmount(updatedAmount: String) {
        amount = updatedAmount
    }
}
