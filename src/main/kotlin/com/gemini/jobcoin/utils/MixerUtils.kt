package com.gemini.jobcoin.utils

object MixerUtils {
    private const val STRING_LENGTH = 10
    private const val ALPHANUMERIC_REGEX = "[a-zA-Z0-9]+"
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    private val usedMixerAddresses = mutableSetOf<String>()

    fun generateTemporaryMixerDepositAddress(): String {
        var randomString = generateRandomString()

        // Keep track of the temporary mixerAddresses currently in use
        if (usedMixerAddresses.contains(randomString)) {
            randomString = generateRandomString()
        }
        usedMixerAddresses.add(randomString)
        return randomString
    }

    private fun generateRandomString(): String {
        return (1..STRING_LENGTH)
            .map { kotlin.random.Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    // Once a transaction has been complete OR a user session has expired,
    // the temporary mixer address will be removed from rotation.
    fun removeTemporaryMixerAddress(address: String): Boolean {
        return usedMixerAddresses.remove(address)
    }
}
