package com.gemini.jobcoin.controller

import com.gemini.jobcoin.client.JobcoinWebClient
import com.gemini.jobcoin.models.Transaction
import com.gemini.jobcoin.models.api.request.JobcoinTransactionRequest
import com.gemini.jobcoin.models.api.response.AddressInfoResponse
import com.gemini.jobcoin.models.api.response.TransactionPostResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/jobcoin/async/")
class JobcoinAsyncClientController(
    private val jobcoinWebClient: JobcoinWebClient
) {

    @GetMapping("/transactions")
    fun getAllTransactions(): Flux<Transaction> {
        return jobcoinWebClient.getTransactions()
    }

    @PostMapping("/transactions")
    fun postTransaction(
        @RequestParam fromAddress: String,
        @RequestParam toAddress: String,
        @RequestParam amount: String
    ): Mono<TransactionPostResponse> {
        val jobcoinRequest = JobcoinTransactionRequest(fromAddress, toAddress, amount)
        return jobcoinWebClient.postTransaction(jobcoinRequest)
    }

    @GetMapping("/addresses/{address}")
    fun getAddressesInfo(
        @PathVariable address: String
    ): Mono<AddressInfoResponse> {
        return jobcoinWebClient.getAddressInfo(address)
    }
}
