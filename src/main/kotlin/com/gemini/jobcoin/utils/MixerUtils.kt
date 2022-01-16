package com.gemini.jobcoin.utils

object MixerUtils {
    private const val STRING_LENGTH = 10
    private const val ALPHANUMERIC_REGEX = "[a-zA-Z0-9]+"
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun generateNewDepositAddress(): String {
        val randomString = (1..STRING_LENGTH)
            .map { i -> kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")

        return randomString
    }
}
