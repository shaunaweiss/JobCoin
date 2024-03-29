package com.gemini.jobcoin.services

import com.gemini.jobcoin.client.JobcoinWebClient
import com.gemini.jobcoin.models.HOUSE_ADDRESS
import com.gemini.jobcoin.models.api.request.JobcoinTransactionRequest
import com.gemini.jobcoin.models.mixer.MixerTaskStatus
import com.gemini.jobcoin.models.mixer.Task
import com.gemini.jobcoin.utils.MixerUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CoinMixerOrchestrator(
    private val jobcoinWebClient: JobcoinWebClient,
    private val mixerTaskQueueDispatcher: MixingTaskQueueDispatcherDynamic
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    fun processMixingTransactions(tasks: Set<Task>) {
        tasks.forEach { task ->
            // 1. Transfer Funds to House Address
            transferFundsToHouseAddress(task)

            // 2. Mix Funds
            val partitionPercentages = generatePartitionsForTask(task)

            val depositAddressAllocations = MixerUtils.allocateCoinDistributionByPartitionPercentage(
                partitionPercentages = partitionPercentages,
                depositAddresses = task.mixerTransaction.outgoingDepositAddresses,
                amount = task.mixerTransaction.amount!!.toDouble()
            )
            task.updateTaskForMixing(depositAddressAllocations, MixerTaskStatus.ReadyForCoinMixing)

            mixerTaskQueueDispatcher.enqueue(task)
        }
    }

    private fun generatePartitionsForTask(task: Task): DoubleArray {
        val numDepositAddresses = task.mixerTransaction.getNumDepositAddresses()
        return MixerUtils.generatePercentagePartitions(numDepositAddresses)
    }

    private fun transferFundsToHouseAddress(task: Task) {
        // 1. Create new JobcoinTransactionRequest
        val houseJobcoinTransactionRequest = createHouseTransactionRequest(
            fromAddress = task.mixerTransaction.temporaryMixerAddress,
            amount = task.mixerTransaction.amount!!
        )

        // 2. Transfer balance to house.
        jobcoinWebClient.postTransaction(houseJobcoinTransactionRequest)

        // 3. Update Task Status
        task.updateTaskStatus(MixerTaskStatus.FundsInHouse)
        logger.info("Updating task status for $task")
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
