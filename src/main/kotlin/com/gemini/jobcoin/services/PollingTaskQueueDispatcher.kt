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

    /**
     * Scheduled Job to poll the P2P network for the addresses provided by the Mixer to user.
     *
     * Note: Logging statements are purposefully overkill to enhance visibility throughout the function.
     * On a production application, we would certainly want/need to refine these.
     */
    @Scheduled(fixedRate = 10000)
    fun pollP2PNetworkForMixerDepositAddresses() {
        logger.info("Running Scheduled Polling Job")
        val tasksReadyForMixing = mutableSetOf<Task>()
        val backOnTheQueue: Queue<Task> = LinkedList()

        logger.info("Polling for ${pollingTasks.size} mixerDepositAddress transactions")
        logger.debug("Task Queue before polling: $pollingTasks")

        while (pollingTasks.isNotEmpty()) {
            val task = pollingTasks.poll()
            val mixerDepositAddress = task.mixerTransaction.temporaryMixerAddress
            logger.info("Polling for $mixerDepositAddress")

            val addressInfo = jobcoinWebClient.getAddressInfo(mixerDepositAddress)

            if (addressInfo != null && addressInfo.isNonZeroBalance()) {
                task.updateTaskForProcessing(addressInfo.balance, MixerTaskStatus.ReadyForProcessing)
                logger.info("Task with mixerDepositAddress $mixerDepositAddress - Ready For Processing")
                tasksReadyForMixing.add(task)
            } else {
                // If the deposit has not yet been made, roll this task over to be checked again on the
                // next iteration of this scheduled task.
                backOnTheQueue.add(task)
            }
        }
        logger.info("Number of Rollover Polling tasks: ${backOnTheQueue.size}")
        pollingTasks.addAll(backOnTheQueue)
        coinMixerOrchestrator.processMixingTransactions(tasksReadyForMixing)
    }
}
