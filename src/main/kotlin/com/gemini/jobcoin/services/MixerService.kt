package com.gemini.jobcoin.services

import com.gemini.jobcoin.models.mixer.MixerTransaction
import com.gemini.jobcoin.utils.MixerUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MixerService(
    private val mixerTaskSchedulingService: MixerTaskSchedulingService
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    fun mix(depositAddresses: List<String>): MixerTransaction {

        // 1. Generate new deposit address
        val depositAddress = MixerUtils.generateNewDepositAddress()
        // 2. Schedule task for deposit address / transactionId
        val mixerTask = Runnable { poll(depositAddress) }
        val createdTaskId = mixerTaskSchedulingService.addNewTask(mixerTask)

        // 3. return depositAddress + transactionId
        return MixerTransaction(
            outgoingDepositAddresses = listOf(), // TODO
            mixerAddress = depositAddress,
            transactionId = createdTaskId
        )
    }

    fun poll(depositAddress: String) {
        // TODO:  Poll Jobcoin API listening for coins sent to mixerTransaction.transactionId
        logger.info("I am polling Jobcoin API for $depositAddress")

        // Once we see the transaction we are looking for, we need to transfer that money to house address
        // add the task to the task queue
    }
}
