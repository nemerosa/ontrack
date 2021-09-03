package net.nemerosa.ontrack.job

import org.apache.commons.lang3.StringUtils
import java.util.concurrent.TimeUnit

data class Schedule(
        val initialPeriod: Long = 0,
        val period: Long,
        val unit: TimeUnit
) {
    val periodText: String
        get() = when {
            period <= 0 -> "Manually"
            period == 1L -> "Every " + StringUtils.substringBeforeLast(unit.name.lowercase(), "s")
            else -> "Every " + period + " " + unit.name.lowercase()
        }

    fun toMiliseconds(): Long = TimeUnit.MILLISECONDS.convert(period, unit)

    fun after(initial: Int): Schedule {
        return Schedule(
                initial.toLong(),
                period,
                unit
        )
    }

    companion object {

        @JvmStatic
        fun everySeconds(seconds: Long): Schedule {
            return Schedule(0, seconds, TimeUnit.SECONDS)
        }

        @JvmStatic
        fun everyMinutes(minutes: Long): Schedule {
            return Schedule(0, minutes, TimeUnit.MINUTES)
        }

        @JvmField
        val NONE = everySeconds(0)
        @JvmField
        val EVERY_SECOND = everySeconds(1)
        @JvmField
        val EVERY_MINUTE = everyMinutes(1)
        @Suppress("unused")
        @JvmField
        val EVERY_HOUR = Schedule(0, 1, TimeUnit.HOURS)
        @JvmField
        val EVERY_DAY = Schedule(0, 1, TimeUnit.DAYS)
        @JvmField
        val EVERY_WEEK = Schedule(0, 7, TimeUnit.DAYS)
    }
}
