package com.gemini.jobcoin.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class StatusController {

    @GetMapping("/")
    fun index(): String {
        return "I'd like to work for Gemini."
    }

    @GetMapping("/status")
    fun status(): String {
        return "Hopefully my interview is going A-OK!"
    }
}
