package com.gemini.jobcoin.models.mixer

import java.time.Instant

class MixerTask(
    val mixerTransaction: MixerTransaction,
    var status: MixerJobStatus = MixerJobStatus.AwaitingDeposit,
    var lastUpdated: Instant = Instant.now()
) {
    fun updateJobForProcessing(amount: String) {
        mixerTransaction.updateTransactionAmount(amount)
        status = MixerJobStatus.ReadyForProcessing
        lastUpdated = Instant.now()
    }
}
