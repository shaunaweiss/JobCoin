package com.gemini.jobcoin.controller

import com.gemini.jobcoin.client.JobcoinRestTemplateClient
import com.gemini.jobcoin.models.Transaction
import com.gemini.jobcoin.models.api.request.JobcoinTransactionRequest
import com.gemini.jobcoin.models.api.response.AddressInfoResponse
import com.gemini.jobcoin.models.api.response.TransactionPostResponse
import com.gemini.jobcoin.services.MixerService
import com.gemini.jobcoin.services.MixerTaskSchedulingService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/jobcoin")
class JobcoinController(
    private val jobcoinRestTemplateClient: JobcoinRestTemplateClient,
    private val mixerTaskSchedulingService: MixerTaskSchedulingService,
    private val mixerService: MixerService
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/transactions")
    fun getAllTransactions(): List<Transaction>? {
        return jobcoinRestTemplateClient.getTransactions()
    }

    @PostMapping("/transactions")
    fun postTransaction(
        @RequestParam fromAddress: String,
        @RequestParam toAddress: String,
        @RequestParam amount: String
    ): TransactionPostResponse? {
        val jobcoinRequest = JobcoinTransactionRequest(fromAddress, toAddress, amount)
        return jobcoinRestTemplateClient.postTransaction(jobcoinRequest)
    }

    @GetMapping("/addresses/{address}")
    fun getAddressesInfo(
        @PathVariable address: String
    ): AddressInfoResponse? {
        return jobcoinRestTemplateClient.getAddressInfo(address)
    }
}
