package com.gemini.jobcoin.models.mixer

data class MixerTransaction(
    val mixerAddress: String, // refer to this as a temporary address
    val transactionId: Int,
    val outgoingDepositAddresses: List<String>
)
