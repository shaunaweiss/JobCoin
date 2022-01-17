package com.gemini.jobcoin.controller

import com.gemini.jobcoin.client.JobcoinWebClient
import com.gemini.jobcoin.models.api.Transaction
import com.gemini.jobcoin.models.api.request.JobcoinTransactionRequest
import com.gemini.jobcoin.models.api.response.AddressInfoResponse
import com.gemini.jobcoin.models.api.response.TransactionPostResponse
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Used this class for testing/debugging while setting up the JobcoinWebClient.
 */

@RestController
@RequestMapping("/jobcoin")
class JobcoinController(
    private val jobcoinWebClient: JobcoinWebClient
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/transactions")
    fun getAllTransactions(): Flux<Transaction> {
        return jobcoinWebClient.getTransactions()
    }

    // Current Assumptions: The user making request has sufficient funds in his/her account.
    // TODO: Add in some handling around this.
    @PostMapping("/transactions")
    fun postTransaction(
        @RequestParam fromAddress: String,
        @RequestParam toAddress: String,
        @RequestParam amount: String
    ): Mono<TransactionPostResponse> {
        val jobcoinRequest = JobcoinTransactionRequest(fromAddress, toAddress, amount)
        return jobcoinWebClient.postTransactionAsync(jobcoinRequest)
    }

    @GetMapping("/addresses/{address}")
    fun getAddressesInfo(
        @PathVariable address: String
    ): Mono<AddressInfoResponse> {
        return jobcoinWebClient.getAddressInfoAsync(address)
    }
}
