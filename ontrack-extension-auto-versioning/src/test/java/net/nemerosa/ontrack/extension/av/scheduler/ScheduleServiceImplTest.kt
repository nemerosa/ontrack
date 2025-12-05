package net.nemerosa.ontrack.extension.av.scheduler

import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class ScheduleServiceImplTest {

    private val scheduleService = ScheduleServiceImpl()

    @Test
    fun `nextExecutionTime with every minute cron`() {
        val now = LocalDateTime.of(2025, 12, 5, 10, 30, 0)
        val next = scheduleService.nextExecutionTime("0 * * * * *", now)
        assertEquals(LocalDateTime.of(2025, 12, 5, 10, 31, 0), next)
    }

    @Test
    fun `nextExecutionTime with hourly cron`() {
        val now = LocalDateTime.of(2025, 12, 5, 10, 30, 0)
        val next = scheduleService.nextExecutionTime("0 0 * * * *", now)
        assertEquals(LocalDateTime.of(2025, 12, 5, 11, 0, 0), next)
    }

    @Test
    fun `nextExecutionTime with daily cron at midnight`() {
        val now = LocalDateTime.of(2025, 12, 5, 10, 30, 0)
        val next = scheduleService.nextExecutionTime("0 0 0 * * *", now)
        assertEquals(LocalDateTime.of(2025, 12, 6, 0, 0, 0), next)
    }

    @Test
    fun `nextExecutionTime with specific time`() {
        val now = LocalDateTime.of(2025, 12, 5, 10, 30, 0)
        val next = scheduleService.nextExecutionTime("0 0 15 * * *", now)
        assertEquals(LocalDateTime.of(2025, 12, 5, 15, 0, 0), next)
    }

    @Test
    fun `nextExecutionTime with past time today should return tomorrow`() {
        val now = LocalDateTime.of(2025, 12, 5, 16, 30, 0)
        val next = scheduleService.nextExecutionTime("0 0 15 * * *", now)
        assertEquals(LocalDateTime.of(2025, 12, 6, 15, 0, 0), next)
    }

    @Test
    fun `nextExecutionTime with every 5 minutes`() {
        val now = LocalDateTime.of(2025, 12, 5, 10, 32, 0)
        val next = scheduleService.nextExecutionTime("0 */5 * * * *", now)
        assertEquals(LocalDateTime.of(2025, 12, 5, 10, 35, 0), next)
    }

    @Test
    fun `nextExecutionTime with weekday cron on Friday should be same day`() {
        val now = LocalDateTime.of(2025, 12, 5, 10, 0, 0) // Friday
        val next = scheduleService.nextExecutionTime("0 0 15 * * MON-FRI", now)
        assertEquals(LocalDateTime.of(2025, 12, 5, 15, 0, 0), next)
    }

    @Test
    fun `nextExecutionTime with weekday cron on Friday evening should be next Monday`() {
        val now = LocalDateTime.of(2025, 12, 5, 18, 0, 0) // Friday evening
        val next = scheduleService.nextExecutionTime("0 0 15 * * MON-FRI", now)
        assertEquals(LocalDateTime.of(2025, 12, 8, 15, 0, 0), next) // Monday
    }

    @Test
    fun `nextExecutionTime with month-specific cron`() {
        val now = LocalDateTime.of(2025, 12, 5, 10, 0, 0)
        val next = scheduleService.nextExecutionTime("0 0 0 1 1 *", now)
        assertEquals(LocalDateTime.of(2026, 1, 1, 0, 0, 0), next)
    }

    @Test
    fun `nextExecutionTime at exact cron time returns next occurrence`() {
        val now = LocalDateTime.of(2025, 12, 5, 10, 0, 0)
        val next = scheduleService.nextExecutionTime("0 0 10 * * *", now)
        // Should return the next day at 10:00, not the same time
        assertTrue(next.isAfter(now))
    }

    @Test
    fun `nextExecutionTime with invalid cron expression throws exception`() {
        val now = LocalDateTime.of(2025, 12, 5, 10, 0, 0)
        assertFailsWith<IllegalArgumentException> {
            scheduleService.nextExecutionTime("invalid cron", now)
        }
    }

    @Test
    fun `nextExecutionTime with 6-field cron format`() {
        val now = LocalDateTime.of(2025, 12, 5, 10, 30, 0)
        // Spring cron format: second minute hour day-of-month month day-of-week
        val next = scheduleService.nextExecutionTime("0 0 12 * * *", now)
        assertEquals(LocalDateTime.of(2025, 12, 5, 12, 0, 0), next)
    }
}
