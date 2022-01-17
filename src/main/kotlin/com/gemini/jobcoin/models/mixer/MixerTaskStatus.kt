package com.gemini.jobcoin.models.mixer

enum class MixerTaskStatus {
    AwaitingDeposit,
    ReadyForProcessing,
    FundsInHouse,
    ReadyForCoinMixing,
    CoinMixed,
    Unknown
}
