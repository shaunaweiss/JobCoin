package com.gemini.jobcoin.config

import com.gemini.jobcoin.services.MixingTaskQueueDispatcherDynamic
import java.time.Instant
import java.util.Date
import java.util.Optional
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.function.Supplier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.SchedulingConfigurer
import org.springframework.scheduling.config.ScheduledTaskRegistrar

@Configuration
@EnableScheduling
open class DynamicSchedulingConfig(
    private val mixingTaskQueueDispatcherDynamic: MixingTaskQueueDispatcherDynamic
) : SchedulingConfigurer {

    @Bean
    open fun taskExecutor(): Executor? {
        return Executors.newSingleThreadScheduledExecutor()
    }

    override fun configureTasks(
        taskRegistrar: ScheduledTaskRegistrar
    ) {
        taskRegistrar.setScheduler(taskExecutor())
        taskRegistrar.addTriggerTask(
            { mixingTaskQueueDispatcherDynamic.mixToDepositAddresses() }
        ) { context ->
            val lastCompletionTime: Optional<Date> =
                Optional.ofNullable(context.lastCompletionTime())
            val nextExecutionTime: Instant =
                lastCompletionTime.orElseGet(Supplier { Date() }).toInstant()
                    .plusMillis(mixingTaskQueueDispatcherDynamic.getRandomDelay())
            Date.from(nextExecutionTime)
        }
    }
}
