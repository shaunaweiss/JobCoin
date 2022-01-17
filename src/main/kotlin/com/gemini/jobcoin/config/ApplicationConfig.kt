package com.gemini.jobcoin.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.gemini.jobcoin.exceptions.ConfigLoadingException
import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import java.time.Clock
import java.time.Duration
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.Connection
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.TcpClient

@Configuration
@EnableScheduling
open class ApplicationConfig {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    open fun clock(): Clock {
        return Clock.systemUTC()
    }

    @Bean
    open fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper()
            .registerModule(JavaTimeModule())
            .registerModule(Jdk8Module())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    @Bean
    open fun webClient(
        @Value("\${jobcoin.base-uri}") baseUri: String,
        @Value("\${jobcoin.read-timeout}") readTimeout: Duration,
        @Value("\${jobcoin.write-timeout}") writeTimeout: Duration,
        @Value("\${jobcoin.connection-timeout}") connectionTimeout: Duration
    ): WebClient {
        if (baseUri.isEmpty()) {
            throw ConfigLoadingException("Jobcoin Config baseUri")
        }
        val message = "Creating WebClient with baseUri: $baseUri, " +
            "connectTimeout: $connectionTimeout, readTimeout: $readTimeout"
        logger.info(message)

        return WebClient.builder()
            .baseUrl(baseUri)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient.from( // todo: fix deprecation
                        TcpClient
                            .create()
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout.toMillisPart())
                            .doOnConnected { connection: Connection ->
                                connection.addHandlerLast(ReadTimeoutHandler(readTimeout.toSeconds(), TimeUnit.SECONDS))
                                connection.addHandlerLast(WriteTimeoutHandler(writeTimeout.toSeconds(), TimeUnit.SECONDS))
                            })
                )
            )
            .build()
    }

// todo: change these vals and move to config
}
