package com.gemini.jobcoin.client

import com.gemini.jobcoin.exceptions.InsufficientFundsException
import com.gemini.jobcoin.models.ADDRESSES_URI
import com.gemini.jobcoin.models.TRANSACTIONS_URI
import com.gemini.jobcoin.models.api.Transaction
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
 * Jobcoin Client using Spring's new REST Client- WebClient & Mono/Flux streams.
 */
@Component
class JobcoinWebClient(
    private val webClient: WebClient
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Asynchronous function to POST [JobcoinTransactionRequest] to the Jobcoin API.
     * Note: Unused in current implementation.
     */
    fun postTransactionAsync(
        jobcoinRequest: JobcoinTransactionRequest
    ): Mono<TransactionPostResponse> = webClient
        .post()
        .uri(TRANSACTIONS_URI)
        .body(BodyInserters.fromValue(jobcoinRequest))
        .retrieve()
        .onStatus(HttpStatus.UNPROCESSABLE_ENTITY::equals) { Mono.error(InsufficientFundsException("${it.bodyToMono(String::class.java)}")) }
        .onStatus(HttpStatus::is4xxClientError) { Mono.error(RuntimeException("4XX Error ${it.statusCode()}")) }
        .onStatus(HttpStatus::is5xxServerError) { Mono.error(RuntimeException("5XX Error ${it.statusCode()}")) }
        .bodyToMono(TransactionPostResponse::class.java)

    /**
     * Synchronous function to POST [JobcoinTransactionRequest] to the Jobcoin API.
     */
    fun postTransaction(
        jobcoinRequest: JobcoinTransactionRequest
    ): TransactionPostResponse? = webClient
        .post()
        .uri(TRANSACTIONS_URI)
        .body(BodyInserters.fromValue(jobcoinRequest))
        .retrieve()
        .onStatus(HttpStatus.UNPROCESSABLE_ENTITY::equals) { Mono.error(InsufficientFundsException("${it.bodyToMono(String::class.java)}")) }
        .onStatus(HttpStatus::is4xxClientError) { Mono.error(RuntimeException("4XX Error ${it.statusCode()}")) }
        .onStatus(HttpStatus::is5xxServerError) { Mono.error(RuntimeException("5XX Error ${it.statusCode()}")) }
        .bodyToMono(TransactionPostResponse::class.java)
        .block()

    /**
     * Asynchronous function to GET a collection of[Transaction]s from the Jobcoin API.
     */
    fun getTransactions(): Flux<Transaction> = webClient
        .get()
        .uri(TRANSACTIONS_URI)
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError) { Mono.error(RuntimeException("4XX Error ${it.statusCode()}")) }
        .onStatus(HttpStatus::is5xxServerError) { Mono.error(RuntimeException("5XX Error ${it.statusCode()}")) }
        .bodyToFlux(Transaction::class.java)

    /**
     * Asynchronous function to GET [AddressInfoResponse] from the Jobcoin API.
     * Note: Unused in current implementation.
     */
    fun getAddressInfoAsync(address: String): Mono<AddressInfoResponse> = webClient
        .get()
        .uri("$ADDRESSES_URI/$address")
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError) { Mono.error(RuntimeException("4XX Error ${it.statusCode()}")) }
        .onStatus(HttpStatus::is5xxServerError) { Mono.error(RuntimeException("5XX Error ${it.statusCode()}")) }
        .bodyToMono(AddressInfoResponse::class.java)

    /**
     * Synchronous function to GET [AddressInfoResponse] from the Jobcoin API.
     */
    fun getAddressInfo(address: String): AddressInfoResponse? = webClient
        .get()
        .uri("$ADDRESSES_URI/$address")
        .retrieve()
        .onStatus(HttpStatus::is4xxClientError) { Mono.error(RuntimeException("4XX Error ${it.statusCode()}")) }
        .onStatus(HttpStatus::is5xxServerError) { Mono.error(RuntimeException("5XX Error ${it.statusCode()}")) }
        .bodyToMono(AddressInfoResponse::class.java)
        .block()
}
