package com.gemini.jobcoin.services

import com.gemini.jobcoin.client.JobcoinWebClient
import com.gemini.jobcoin.models.mixer.MixerJob
import java.util.LinkedList
import java.util.Queue
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ScheduleTasks(
    private val jobcoinWebClient: JobcoinWebClient,
    private val coinMixerOrchestrator: CoinMixerOrchestrator
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val mixerJobs: Queue<MixerJob> = LinkedList<MixerJob>()

    fun enqueue(mixerJob: MixerJob) {
        mixerJobs.add(mixerJob)
    }

    private fun dequeue(mixerJob: MixerJob) {
        mixerJobs.remove(mixerJob)
    }

    @Scheduled(fixedRate = 5000)
    fun getTransactionForTempDepositAddress() {
        logger.info("Running Scheduled Polling Job")
        val jobsReadyForMixing = mutableSetOf<MixerJob>()
        mixerJobs.forEach { job ->
            val tempAddress = job.mixerTransaction.temporaryMixerAddress
            val addressInfo = jobcoinWebClient.getAddressInfoSync(tempAddress)
            val balance = addressInfo?.balance
            logger.info("Polling for $tempAddress")
            if (!balance.isNullOrEmpty()) {
                job.updateJobForProcessing(balance)
                jobsReadyForMixing.add(job)
                dequeue(job)
            }
        }
        coinMixerOrchestrator.processMixingTransactions(jobsReadyForMixing)
    }
}
