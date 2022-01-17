package utils

import com.gemini.jobcoin.utils.MixerUtils
import com.gemini.jobcoin.utils.MixerUtils.MAX_PARTITION_PERCENTAGE
import kotlin.random.Random
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test

class TestMixerUtils {

    // @Test
    // fun `Test generatePercentagePartitions() - assert partitions percentages sum to 100 `() {
    //     val expectedSum = MAX_PARTITION_PERCENTAGE
    //     val randomGroupingValues = List(10) { Random.nextInt(1, 100) }
    //     randomGroupingValues.forEach {
    //         val randomPartitions = MixerUtils.generatePercentagePartitions(it)
    //         assertEquals(expectedSum, randomPartitions.sum())
    //     }
    // }
    //
    // @Test
    // fun `Test allocateCoinDistributionByPartitionPercentage() - assert partitions sum to deposit amount`() {
    //     val expectedSum = MAX_PARTITION_PERCENTAGE
    //     val randomGroupingValues = List(10) { Random.nextInt(1, 100) }
    //     randomGroupingValues.forEach {
    //         val randomPartitions = MixerUtils.generatePercentagePartitions(it)
    //         assertEquals(expectedSum, randomPartitions.sum())
    //     }
    // }
}
