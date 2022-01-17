package com.gemini.jobcoin.utils

import org.slf4j.LoggerFactory

object MixerUtils {
    private const val STRING_LENGTH = 10
    const val MAX_PARTITION_PERCENTAGE = 100.0
    private const val ALPHANUMERIC_REGEX = "[a-zA-Z0-9]+"
    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    private val logger = LoggerFactory.getLogger(javaClass)
    private val mixerAddressesInUse = mutableSetOf<String>()

    fun generateTemporaryMixerDepositAddress(): String {
        var randomString = generateRandomString()
        // Keep track of the temporary mixerAddresses currently in use
        if (mixerAddressesInUse.contains(randomString)) {
            randomString = generateRandomString()
        }
        mixerAddressesInUse.add(randomString)
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
        return mixerAddressesInUse.remove(address)
    }

    /**
     * Generates [n] random numbers that will sum 100. These partitions will be used as the percentage of
     * coin to transfer to each outgoingDepositAddress
     */
    fun generatePercentagePartitions(n: Int): DoubleArray {
        val randArray = DoubleArray(n)
        var sum = 0.0

        // Generate n random numbers
        for (i in randArray.indices) {
            randArray[i] = Math.random()
            sum += randArray[i]
        }

        // Normalize sum to m
        for (i in randArray.indices) {
            randArray[i] /= sum
            randArray[i] *= MAX_PARTITION_PERCENTAGE
        }

        return randArray
    }

    fun allocateCoinDistributionByPartitionPercentage(
        partitionPercentages: DoubleArray,
        depositAddresses: List<String>,
        amount: Double
    ): Map<String, Double> {
        val depositAddressAllocation = mutableMapOf<String, Double>()
        if (partitionPercentages.size != depositAddresses.size) {
            logger.error("Error generating the deposit allocation.")
            return mapOf()
        }
        depositAddresses.forEachIndexed { index, address ->
            val partition = partitionPercentages[index]
            val allocation = (partition / 100.00) * amount
            depositAddressAllocation[address] = allocation
        }
        return depositAddressAllocation
    }
}
