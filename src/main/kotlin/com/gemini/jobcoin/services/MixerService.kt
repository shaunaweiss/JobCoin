package com.gemini.jobcoin.services

import com.gemini.jobcoin.models.api.response.MixerTaskStatusResponse
import com.gemini.jobcoin.models.mixer.MixerRequest
import com.gemini.jobcoin.models.mixer.MixerTaskStatus
import com.gemini.jobcoin.models.mixer.MixerTransaction
import com.gemini.jobcoin.models.mixer.Task
import com.gemini.jobcoin.utils.MixerUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class MixerService(
    private val pollingTaskQueueDispatcher: PollingTaskQueueDispatcher
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val processedMixerJobsLedger = mutableMapOf<String, Task>()

    fun mix(mixerRequest: MixerRequest): MixerTransaction {

        // 1. Generate new deposit address
        val temporaryMixerAddress = UUID.randomUUID().toString()

        // Build Task
        val mixerTransaction = MixerTransaction(
            outgoingDepositAddresses = mixerRequest.depositAddresses,
            temporaryMixerAddress = temporaryMixerAddress
        )
        val task = Task(mixerTransaction)
        pollingTaskQueueDispatcher.enqueue(task)

        processedMixerJobsLedger[temporaryMixerAddress] = task

        return mixerTransaction
    }

    // Future Implementation Ideas: Provide User with TransactionId in order to enable status lookup
    // instead of using temporaryAddress
    fun getMixerJobStatus(temporaryAddress: String): MixerTaskStatusResponse {
        val task = processedMixerJobsLedger[temporaryAddress]
        return if (task != null) {
            MixerTaskStatusResponse(
                temporaryDepositAddress = temporaryAddress,
                taskStatus = task.status
            )
        } else {
            logger.info("Cannot find task status for temporaryAddress: $temporaryAddress")
            MixerTaskStatusResponse(
                temporaryDepositAddress = temporaryAddress,
                taskStatus = MixerTaskStatus.Unknown
            )
        }
    }
}
