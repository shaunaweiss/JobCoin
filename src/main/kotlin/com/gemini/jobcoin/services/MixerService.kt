package com.gemini.jobcoin.services

import com.gemini.jobcoin.models.api.response.MixerTaskStatusResponse
import com.gemini.jobcoin.models.mixer.MixerRequest
import com.gemini.jobcoin.models.mixer.MixerTaskStatus
import com.gemini.jobcoin.models.mixer.MixerTransaction
import com.gemini.jobcoin.models.mixer.Task
import com.gemini.jobcoin.utils.MixerUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MixerService(
    private val pollingTaskQueueDispatcher: PollingTaskQueueDispatcher
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val processedMixerJobsLedger = mutableMapOf<String, Task>()

    fun mix(mixerRequest: MixerRequest): MixerTransaction {

        // 1. Generate new deposit address
        val temporaryMixerAddress = MixerUtils.generateTemporaryMixerDepositAddress()
        // 2. Schedule task for deposit address / transactionId
        // val mixerTask = Runnable { poll(temporaryMixerAddress) }
        // val createdTaskId = mixerTaskSchedulingService.addNewTask(mixerTask)

        val mixerTransaction = MixerTransaction(
            outgoingDepositAddresses = mixerRequest.depositAddresses,
            temporaryMixerAddress = temporaryMixerAddress
        )
        val task = Task(mixerTransaction)
        pollingTaskQueueDispatcher.enqueue(task)

        processedMixerJobsLedger[temporaryMixerAddress] = task

        return mixerTransaction
    }

    // Todo: Provide User with TransactionId in order to enable status lookup instead of using temporaryAddress
    // Todo: This still doesn't work yet
    fun getMixerJobStatus(temporaryAddress: String): MixerTaskStatusResponse {
        logger.info("I made it here")
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

    // Todo: Most likely remove this
    // fun poll(mixerTemporaryDepositAddress: String) {
    //     jobcoinWebClient.getAddressInfoAsync(mixerTemporaryDepositAddress)
    //     // TODO:  Poll Jobcoin API listening for coins sent to mixerTransaction.transactionId
    //     logger.info("I am polling Jobcoin API for $mixerTemporaryDepositAddress")
    //
    //     // Once we see the transaction we are looking for, we need to transfer that money to house address
    //     // add the task to the task queue
    // }
}
