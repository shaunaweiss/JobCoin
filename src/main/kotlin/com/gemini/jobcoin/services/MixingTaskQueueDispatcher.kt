package com.gemini.jobcoin.services

import com.gemini.jobcoin.client.JobcoinWebClient
import com.gemini.jobcoin.models.HOUSE_ADDRESS
import com.gemini.jobcoin.models.api.request.JobcoinTransactionRequest
import com.gemini.jobcoin.models.mixer.MixerTaskStatus
import com.gemini.jobcoin.models.mixer.Task
import com.gemini.jobcoin.utils.MixerUtils
import java.util.LinkedList
import java.util.Queue
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MixingTaskQueueDispatcher(
    private val jobcoinWebClient: JobcoinWebClient
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val mixingTasks: Queue<Task> = LinkedList()

    fun enqueue(task: Task) {
        mixingTasks.add(task)
    }

    private fun dequeue(task: Task) {
        mixingTasks.remove(task)
    }

    // Future ideas, make this a random delay
    @Scheduled(fixedRate = 5000)
    fun makeMixedTransactions() {
        logger.info("Running Scheduled Mixing Job")
        mixingTasks.forEach { task ->
            val tempAddress = task.mixerTransaction.temporaryMixerAddress
            logger.info("Mixing for $tempAddress")

            val depositAllocationMap = task.depositAllocationMap

            depositAllocationMap.forEach {
                val transactionRequest = JobcoinTransactionRequest(
                    fromAddress = HOUSE_ADDRESS,
                    toAddress = it.key,
                    amount = it.value.toString()
                )
                logger.info("Posting Transaction: $transactionRequest")
                jobcoinWebClient.postTransaction(transactionRequest)
                // Todo - handle a retry if this fails

                // For future implementation - if a single request failed, send back the amount to the original senderAddress?
            }
            task.updateTaskStatus(MixerTaskStatus.CoinMixed)
            logger.info("MixerTaskStatus: ${task.status}")
            dequeue(task)
            MixerUtils.removeTemporaryMixerAddress(task.mixerTransaction.temporaryMixerAddress)
        }
    }
}
