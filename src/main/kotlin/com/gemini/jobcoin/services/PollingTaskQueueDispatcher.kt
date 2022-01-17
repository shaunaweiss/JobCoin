package com.gemini.jobcoin.services

import com.gemini.jobcoin.client.JobcoinWebClient
import com.gemini.jobcoin.models.mixer.MixerTaskStatus
import com.gemini.jobcoin.models.mixer.Task
import java.util.LinkedList
import java.util.Queue
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class PollingTaskQueueDispatcher(
    private val jobcoinWebClient: JobcoinWebClient,
    private val coinMixerOrchestrator: CoinMixerOrchestrator
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val pollingTasks: Queue<Task> = LinkedList()

    fun enqueue(task: Task) {
        pollingTasks.add(task)
    }

    private fun dequeue(task: Task) {
        pollingTasks.remove(task)
    }

    @Scheduled(fixedRate = 10000)
    fun pollJobcoinApiForTempDepositAddresses() {
        logger.info("Running Scheduled Polling Job")
        val tasksReadyForMixing = mutableSetOf<Task>()
        pollingTasks.forEach { task ->
            val tempAddress = task.mixerTransaction.temporaryMixerAddress
            logger.info("Polling for $tempAddress")

            val addressInfo = jobcoinWebClient.getAddressInfoSync(tempAddress)

            // If there is a balance on the Address Info, that means money has been transferred
            // to the tempAddress
            // Todo: Better Handle these Null checks
            if (addressInfo!!.isNonZeroBalance()) {
                // The only thing missing from a MixerTransaction at this point is the amount.
                task.updateTaskForProcessing(addressInfo.balance!!, MixerTaskStatus.ReadyForProcessing)
                tasksReadyForMixing.add(task)
                dequeue(task)
            }
        }
        coinMixerOrchestrator.processMixingTransactions(tasksReadyForMixing)
    }
}
