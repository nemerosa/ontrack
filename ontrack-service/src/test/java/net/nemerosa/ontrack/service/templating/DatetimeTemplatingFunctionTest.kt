package net.nemerosa.ontrack.service.templating

import net.nemerosa.ontrack.common.MockTime
import net.nemerosa.ontrack.model.events.PlainEventRenderer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import java.time.Month
import kotlin.test.assertEquals

class DatetimeTemplatingFunctionTest {

    private val datetimeTemplatingFunction: DatetimeTemplatingFunction = DatetimeTemplatingFunction(
        timeServer = MockTime,
    )

    @BeforeEach
    fun init() {
        MockTime.clock = LocalDateTime.of(2024, Month.APRIL, 16, 16, 24, 48)
    }

    @Test
    fun `Current default time`() {
        val text = datetimeTemplatingFunction.render(
            configMap = emptyMap(),
            context = emptyMap(),
            renderer = PlainEventRenderer.INSTANCE,
            expressionResolver = { it },
        )
        assertEquals(
            "2024-04-16T16:24:48",
            text
        )
    }

    @Test
    fun `Current default time for a specific time zone`() {
        val text = datetimeTemplatingFunction.render(
            configMap = mapOf(
                "timezone" to "Europe/Brussels",
            ),
            context = emptyMap(),
            renderer = PlainEventRenderer.INSTANCE,
            expressionResolver = { it },
        )
        assertEquals(
            "2024-04-16T18:24:48",
            text
        )
    }

    @Test
    fun `Custom format`() {
        val text = datetimeTemplatingFunction.render(
            configMap = mapOf(
                "format" to "yyyy-MM-dd"
            ),
            context = emptyMap(),
            renderer = PlainEventRenderer.INSTANCE,
            expressionResolver = { it },
        )
        assertEquals(
            "2024-04-16",
            text
        )
    }

    @Test
    fun `Custom format and adding one day`() {
        val text = datetimeTemplatingFunction.render(
            configMap = mapOf(
                "format" to "yyyy-MM-dd",
                "days" to "1",
            ),
            context = emptyMap(),
            renderer = PlainEventRenderer.INSTANCE,
            expressionResolver = { it },
        )
        assertEquals(
            "2024-04-17",
            text
        )
    }

    @Test
    fun `Custom format and removing one month`() {
        val text = datetimeTemplatingFunction.render(
            configMap = mapOf(
                "format" to "yyyy-MM-dd",
                "months" to "-1",
            ),
            context = emptyMap(),
            renderer = PlainEventRenderer.INSTANCE,
            expressionResolver = { it },
        )
        assertEquals(
            "2024-03-16",
            text
        )
    }

}