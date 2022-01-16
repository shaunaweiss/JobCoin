package com.gemini.jobcoin.client

import com.gemini.jobcoin.exceptions.InsufficientFundsException
import com.gemini.jobcoin.models.Transaction
import com.gemini.jobcoin.models.api.request.JobcoinTransactionRequest
import com.gemini.jobcoin.models.api.response.AddressInfoResponse
import com.gemini.jobcoin.models.api.response.TransactionPostResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

/**
 * Asynchronous Jobcoin Client using Spring's new REST Client- WebClient & Mono streams.
 */
@Component
class JobcoinWebClient(
    private val webClient: WebClient
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun postTransaction(
        jobcoinRequest: JobcoinTransactionRequest
    ): Mono<TransactionPostResponse> = webClient
        .post()
        .uri("/transactions")
        .body(BodyInserters.fromValue(jobcoinRequest))
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError) { Mono.error(RuntimeException("4XX Error ${it.statusCode()}")) } // todo handle actual 422 error
        .onStatus(HttpStatus::is5xxServerError) { Mono.error(RuntimeException("5XX Error ${it.statusCode()}")) }
        .bodyToMono(TransactionPostResponse::class.java)

    fun getTransactions(): Flux<Transaction> = webClient
        .get()
        .uri("/transactions")
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError) { Mono.error(InsufficientFundsException("4XX Error ${it.statusCode()}")) }
        .onStatus(HttpStatus::is5xxServerError) { Mono.error(RuntimeException("5XX Error ${it.statusCode()}")) }
        .bodyToFlux(Transaction::class.java)

    fun getAddressInfo(address: String): Mono<AddressInfoResponse> = webClient
        .get()
        .uri("/addresses/$address")
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError) { Mono.error(RuntimeException("4XX Error ${it.statusCode()}")) }
        .onStatus(HttpStatus::is5xxServerError) { Mono.error(RuntimeException("5XX Error ${it.statusCode()}")) }
        .bodyToMono(AddressInfoResponse::class.java)
}
