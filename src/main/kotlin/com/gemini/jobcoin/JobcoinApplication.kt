package com.gemini.jobcoin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder

@SpringBootApplication
open class JobcoinApplication {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SpringApplicationBuilder()
                .sources(JobcoinApplication::class.java)
                .run(*args)
        }
    }
}
