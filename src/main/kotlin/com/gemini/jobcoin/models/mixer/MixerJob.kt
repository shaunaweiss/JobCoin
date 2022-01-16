package com.gemini.jobcoin.models.mixer

class MixerJob(
    val mixerTransaction: MixerTransaction,
    val status: MixerJobStatus = MixerJobStatus.AwaitingDeposit
)
