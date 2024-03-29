package com.gemini.jobcoin.models.mixer

import java.time.Instant

data class Task(
    val mixerTransaction: MixerTransaction,
    var status: MixerTaskStatus = MixerTaskStatus.AwaitingDeposit,
    var lastUpdated: Instant = Instant.now(),
    val depositAllocationMap: MutableMap<String, Double> = mutableMapOf()
) {
    private fun updateLastUpdated() {
        lastUpdated = Instant.now()
    }
    /**
     * When a [MixerTransaction] is first created, all the properties can be properly instantiated except
     * for the [updatedAmount]. Once the transfer has been initiated to the [temporaryMixerAddress], the balance
     * will be updated in the MixerTask, as well [lastUpdated] & the task [updatedStatus].
     */
    fun updateTaskForProcessing(updatedAmount: String, updatedStatus: MixerTaskStatus) {
        mixerTransaction.updateTransactionAmount(updatedAmount)
        status = updatedStatus
        updateLastUpdated()
    }

    fun updateTaskStatus(updatedStatus: MixerTaskStatus) {
        status = updatedStatus
        updateLastUpdated()
    }

    fun updateTaskForMixing(
        allocationMap: Map<String, Double>,
        updatedStatus: MixerTaskStatus
    ) {
        status = updatedStatus
        updateLastUpdated()
        depositAllocationMap.putAll(allocationMap)
    }
}
