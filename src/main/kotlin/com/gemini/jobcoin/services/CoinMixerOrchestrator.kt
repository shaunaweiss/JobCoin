package com.gemini.jobcoin.services

import com.gemini.jobcoin.client.JobcoinWebClient
import com.gemini.jobcoin.models.mixer.MixerTask
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CoinMixerOrchestrator(
    private val jobcoinWebClient: JobcoinWebClient
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    fun processMixingTransactions(mixerTasks: Set<MixerTask>) {
        mixerTasks.forEach {
            logger.info("I am ready to process ${it.mixerTransaction.temporaryMixerAddress}")
        }
        // Mix coins
    }
}
