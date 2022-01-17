package com.gemini.jobcoin.services

import com.gemini.jobcoin.client.JobcoinWebClient
import com.gemini.jobcoin.models.mixer.MixerJob
import com.gemini.jobcoin.models.mixer.MixerRequest
import com.gemini.jobcoin.models.mixer.MixerTransaction
import com.gemini.jobcoin.utils.MixerUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MixerService(
    private val mixerTaskSchedulingService: MixerTaskSchedulingService,
    private val jobcoinWebClient: JobcoinWebClient,
    private val scheduleTasks: ScheduleTasks
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val processedMixeJobsLedger = mutableSetOf<MixerJob>()

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
        val mixerJob = MixerJob(mixerTransaction)
        scheduleTasks.enqueue(mixerJob)

        return mixerTransaction
    }

    // fun poll(mixerTemporaryDepositAddress: String) {
    //     jobcoinWebClient.getAddressInfoAsync(mixerTemporaryDepositAddress)
    //     // TODO:  Poll Jobcoin API listening for coins sent to mixerTransaction.transactionId
    //     logger.info("I am polling Jobcoin API for $mixerTemporaryDepositAddress")
    //
    //     // Once we see the transaction we are looking for, we need to transfer that money to house address
    //     // add the task to the task queue
    // }
}
