package com.gemini.jobcoin.services

import com.gemini.jobcoin.client.JobcoinWebClient
import com.gemini.jobcoin.models.HOUSE_ADDRESS
import com.gemini.jobcoin.models.api.request.JobcoinTransactionRequest
import com.gemini.jobcoin.models.mixer.MixerTaskStatus
import com.gemini.jobcoin.models.mixer.Task
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

    // Future implementation ideas: Design a delay for each mixing task.
    @Scheduled(fixedRate = 5000)
    fun mixToDepositAddresses() {
        logger.info("Running Scheduled Mixing Job")
        while(mixingTasks.isNotEmpty()) {
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
                // Future implementation - handle if the post request fails
                // If this request failed, we could re-enqueue it to the queue such that it is tried during a
                // later iteration of the scheduled job.

                // Future implementation - If a post transaction request continues to fail after multiple retries,
                // send the needed amount to another one user provided depositAddresses OR to the original senderAddress
                // (right now we don't store anything about the original fromAddress - which I happen to think is a good thing)
            }

            task.updateTaskStatus(MixerTaskStatus.CoinMixed)
            logger.info("MixerTask with mixerDepositAddress: $tempAddress Status: ${task.status}")
        }
    }
}
