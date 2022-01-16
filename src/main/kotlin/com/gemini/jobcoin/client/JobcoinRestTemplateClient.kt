package com.gemini.jobcoin.client

import com.gemini.jobcoin.models.Transaction
import com.gemini.jobcoin.models.api.request.JobcoinTransactionRequest
import com.gemini.jobcoin.models.api.response.AddressInfoResponse
import com.gemini.jobcoin.models.api.response.TransactionPostResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.client.RestTemplate

/**
 * Synchronous Jobcoin Client using Spring's RestTemplate
 */
@Component
class JobcoinRestTemplateClient(
    private val restTemplate: RestTemplate
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    fun getAddressInfo(address: String): AddressInfoResponse? {
        return try {
            val response = restTemplate.exchange(
                "/addresses/$address",
                HttpMethod.GET,
                null,
                AddressInfoResponse::class.java
            )
            response.body
        } catch (e: RestClientResponseException) {
            logger.error("Unable to get address info for address $address, statusCode: ${e.rawStatusCode}", e)
            null
        } catch (t: Throwable) {
            logger.error("Unexpected error making jobcoin request for address $address", t)
            null
        }
    }

    fun getTransactions(): List<Transaction>? {
        return try {
            val response = restTemplate.exchange(
                "/transactions",
                HttpMethod.GET,
                null,
                Array<Transaction>::class.java
            )
            response.body.toList()
        } catch (e: RestClientResponseException) {
            logger.error("Unable to get transactions, statusCode: ${e.rawStatusCode}", e)
            null
        } catch (t: Throwable) {
            logger.error("Unexpected error making transactions request", t)
            null
        }
    }

    fun postTransaction(
        jobcoinRequest: JobcoinTransactionRequest
    ): TransactionPostResponse? {
        return restTemplate.postForObject(
                "/transactions",
                jobcoinRequest,
                TransactionPostResponse::class.java
            )
    }
}
