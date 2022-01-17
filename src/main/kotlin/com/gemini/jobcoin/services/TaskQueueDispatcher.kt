package com.gemini.jobcoin.services

import com.gemini.jobcoin.client.JobcoinWebClient
import com.gemini.jobcoin.models.mixer.MixerTask
import java.util.LinkedList
import java.util.Queue
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TaskQueueDispatcher(
    private val jobcoinWebClient: JobcoinWebClient,
    private val coinMixerOrchestrator: CoinMixerOrchestrator
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val mixerTasks: Queue<MixerTask> = LinkedList<MixerTask>()

    fun enqueue(mixerTask: MixerTask) {
        mixerTasks.add(mixerTask)
    }

    private fun dequeue(mixerTask: MixerTask) {
        mixerTasks.remove(mixerTask)
    }

    @Scheduled(fixedRate = 5000)
    fun getTransactionForTempDepositAddress() {
        logger.info("Running Scheduled Polling Job")
        val tasksReadyForMixing = mutableSetOf<MixerTask>()
        mixerTasks.forEach { job ->
            val tempAddress = job.mixerTransaction.temporaryMixerAddress
            val addressInfo = jobcoinWebClient.getAddressInfoSync(tempAddress)
            val balance = addressInfo?.balance
            logger.info("Polling for $tempAddress")
            if (!balance.isNullOrEmpty()) {
                job.updateJobForProcessing(balance)
                tasksReadyForMixing.add(job)
                dequeue(job)
            }
        }
        coinMixerOrchestrator.processMixingTransactions(tasksReadyForMixing)
    }
}
