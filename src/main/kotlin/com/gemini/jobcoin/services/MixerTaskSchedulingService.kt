package com.gemini.jobcoin.services

import com.gemini.jobcoin.models.mixer.MixerJob
import java.time.Duration
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.atomic.AtomicInteger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service

@Service
class MixerTaskSchedulingService(
    private val scheduler: TaskScheduler
) {
    val mixerJobs: Queue<MixerJob> = LinkedList<MixerJob>()

    private val logger = LoggerFactory.getLogger(javaClass)
    private val futures: MutableMap<Int, ScheduledFuture<*>> = HashMap()
    private val taskId = AtomicInteger()

    fun addNewTask(task: Runnable): Int {
        val taskPeriod = Duration.ofSeconds(15)
        val scheduledTaskFuture = scheduler.scheduleAtFixedRate(task, taskPeriod)

        val id = taskId.incrementAndGet()
        futures[id] = scheduledTaskFuture

        return id
    }

    fun removeTaskFromScheduler(id: Int) {
        futures[id]?.let {
            it.cancel(true)
            futures.remove(id)
        }
    }
}
