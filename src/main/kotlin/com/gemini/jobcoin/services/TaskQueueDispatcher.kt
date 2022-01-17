package com.gemini.jobcoin.services

import com.gemini.jobcoin.client.JobcoinWebClient
import com.gemini.jobcoin.models.mixer.MixerTask
import com.gemini.jobcoin.models.mixer.MixerTaskStatus
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
    private val mixerTasks: Queue<MixerTask> = LinkedList()

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
        mixerTasks.forEach { task ->
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
