package com.gemini.jobcoin.controller

import com.gemini.jobcoin.models.api.response.MixerTaskStatusResponse
import com.gemini.jobcoin.models.mixer.MixerRequest
import com.gemini.jobcoin.models.mixer.MixerTransaction
import com.gemini.jobcoin.services.MixerService
import javax.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/mixer")
class MixerController(
    private val mixerService: MixerService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Endpoint to kick off Jobcoin Mixing
     * @return MixerTransaction
     */
    @PostMapping("/mix")
    fun mix(
        @Valid @RequestBody mixerRequest: MixerRequest
    ): MixerTransaction {
        return mixerService.mix(mixerRequest)
    }

    /**
     * Endpoint used to check in on the progress of a Jobcoin Mixing Task
     */
    @GetMapping("/address/{address}/status")
    fun getTaskStatus(
        @PathVariable address: String
    ): MixerTaskStatusResponse {
        return mixerService.getMixerJobStatus(address)
    }
}
