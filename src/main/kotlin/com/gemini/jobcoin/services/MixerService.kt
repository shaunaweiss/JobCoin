package com.gemini.jobcoin.services

import com.gemini.jobcoin.client.JobcoinWebClient
import com.gemini.jobcoin.models.mixer.MixerTask
import com.gemini.jobcoin.models.mixer.MixerRequest
import com.gemini.jobcoin.models.mixer.MixerTransaction
import com.gemini.jobcoin.utils.MixerUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MixerService(
    private val mixerTaskSchedulingService: MixerTaskSchedulingService,
    private val jobcoinWebClient: JobcoinWebClient,
    private val taskQueueDispatcher: TaskQueueDispatcher
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val processedMixeJobsLedger = mutableSetOf<MixerTask>()

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
        val mixerTask = MixerTask(mixerTransaction)
        taskQueueDispatcher.enqueue(mixerTask)

        return mixerTransaction
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
