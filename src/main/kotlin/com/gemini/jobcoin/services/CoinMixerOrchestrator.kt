package com.gemini.jobcoin.services

import com.gemini.jobcoin.client.JobcoinWebClient
import com.gemini.jobcoin.models.api.request.JobcoinTransactionRequest
import com.gemini.jobcoin.models.mixer.MixerTask
import com.gemini.jobcoin.models.mixer.MixerTaskStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

const val HOUSE_ADDRESS = "house"
@Component
class CoinMixerOrchestrator(
    private val jobcoinWebClient: JobcoinWebClient
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    fun processMixingTransactions(mixerTasks: Set<MixerTask>) {
        mixerTasks.forEach { task ->
            // 1. Transfer Funds to House Address
            transferFundsToHouseAddress(task)

            // 2. Mix Funds
            logger.info("I am ready to mix these funds}")
        }
        // Mix coins
    }

    private fun transferFundsToHouseAddress(task: MixerTask) {
        // 1. Create new JobcoinTransactionRequest
        val houseJobcoinTransactionRequest = createHouseTransactionRequest(
            fromAddress = task.mixerTransaction.temporaryMixerAddress,
            amount = task.mixerTransaction.amount!!
        )

        // 2. Transfer balance to house.
        val postTransaction = jobcoinWebClient.postTransactionSync(houseJobcoinTransactionRequest)

        // 3. Update Task Status
        task.updateTaskStatus(MixerTaskStatus.FundsInHouse)
        logger.info("Task $task")
    }

    private fun createHouseTransactionRequest(
        amount: String,
        fromAddress: String
    ): JobcoinTransactionRequest {
        return JobcoinTransactionRequest(
            fromAddress = fromAddress,
            toAddress = HOUSE_ADDRESS,
            amount = amount
        )
    }
}
