package com.gemini.jobcoin.utils

import org.slf4j.LoggerFactory

object MixerUtils {
    private const val MAX_PARTITION_PERCENTAGE = 100.0
    private val logger = LoggerFactory.getLogger(javaClass)

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

    // General assumption: it IS possible that the amount of coin dolled out to an individual depositAddress is ~ 0
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
