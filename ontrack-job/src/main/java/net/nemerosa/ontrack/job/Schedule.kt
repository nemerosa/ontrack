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
            period == 1L -> "Every " + StringUtils.substringBeforeLast(unit.name.toLowerCase(), "s")
            else -> "Every " + period + " " + unit.name.toLowerCase()
        }

    fun toMiliseconds(): Long = TimeUnit.MILLISECONDS.convert(period, unit)

    fun after(initial: Int): Schedule {
        return Schedule(
                initial.toLong(),
                period,
                unit
        )
    }

    fun sameDelayThan(schedule: Schedule): Boolean {
        return this.period == schedule.period && this.unit == schedule.unit
    }

    fun convertTo(target: TimeUnit): Schedule {
        return Schedule(
                target.convert(initialPeriod, unit),
                target.convert(period, unit),
                target
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
        @JvmField
        val EVERY_DAY = Schedule(0, 1, TimeUnit.DAYS)
    }
}
