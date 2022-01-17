package com.gemini.jobcoin.models.api.response

import com.gemini.jobcoin.models.mixer.MixerTaskStatus

data class MixerTaskStatusResponse(
    val temporaryDepositAddress: String,
    val taskStatus: MixerTaskStatus
)
