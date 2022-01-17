package com.gemini.jobcoin.models.api.response

import com.gemini.jobcoin.models.mixer.MixerTaskStatus

data class MixerTaskStatusResponse(
    private val temporaryDepositAddress: String,
    private val taskStatus: MixerTaskStatus
)
