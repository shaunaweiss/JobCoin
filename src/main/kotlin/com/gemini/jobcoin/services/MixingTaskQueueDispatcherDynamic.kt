package com.gemini.jobcoin.services

import com.gemini.jobcoin.client.JobcoinWebClient
import com.gemini.jobcoin.models.HOUSE_ADDRESS
import com.gemini.jobcoin.models.api.request.JobcoinTransactionRequest
import com.gemini.jobcoin.models.mixer.MixerTaskStatus
import com.gemini.jobcoin.models.mixer.Task
import java.util.LinkedList
import java.util.Queue
import kotlin.random.Random
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class MixingTaskQueueDispatcherDynamic(
    private val jobcoinWebClient: JobcoinWebClient
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val mixingTasks: Queue<Task> = LinkedList()

    fun enqueue(task: Task) {
        mixingTasks.add(task)
    }

    fun getRandomDelay(): Long {
        // Max 10-minute delay 600000
        // val delay = Random.nextLong(0,  600000)
        val delay = Random.nextLong(0, 5000) // For Testing
        logger.info("Scheduled Mixing Job - delaying $delay milliseconds...")
        return delay
    }

    // This task will be dynamically scheduled
    fun mixToDepositAddresses() {
        logger.info("Running Scheduled Mixing Job")
        while (mixingTasks.isNotEmpty()) {
            val task = mixingTasks.poll()
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
                jobcoinWebClient.postTransaction(transactionRequest) // Assumption - this always succeeds :)
            }

            task.updateTaskStatus(MixerTaskStatus.CoinMixed)
            logger.info("MixerTask with mixerDepositAddress: $tempAddress Status: ${task.status}")
        }
    }
}
