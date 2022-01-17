package com.gemini.jobcoin.models.mixer

data class MixerTransaction(
    val temporaryMixerAddress: String,
    // val transactionId: Int,
    val outgoingDepositAddresses: List<String>,
    var amount: String? = null
) {
    fun getNumDepositAddresses(): Int {
        return outgoingDepositAddresses.size
    }

    fun updateTransactionAmount(updatedAmount: String) {
        amount = updatedAmount
    }

    // fun getDoubleAmount(): Double {
    //     return amount!!.toDouble()
    // }
}
