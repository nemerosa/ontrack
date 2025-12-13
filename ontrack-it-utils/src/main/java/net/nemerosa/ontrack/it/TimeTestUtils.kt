package net.nemerosa.ontrack.it

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.common.seconds
import org.slf4j.LoggerFactory
import java.util.concurrent.TimeoutException
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

/**
 * Waits until a given condition is met.
 *
 * @param message Activity being waited for
 * @param initial Initial time to wait
 * @param interval Interval to wait between two attemps
 * @param timeout Total timeout for the operatiion to complete
 * @param ignoreExceptions Set to `true` if exceptions do not abort the wait
 * @param check Must return `true` when the wait is over
 * @receiver Message to associate with the waiting (name of the task)
 */
@ExperimentalTime
fun waitUntil(
    message: String,
    initial: Duration? = null,
    interval: Duration = 5.seconds,
    timeout: Duration = 60.seconds,
    ignoreExceptions: Boolean = false,
    check: () -> Boolean
) {
    TimeTestUtils().waitUntil(message, initial, interval, timeout, ignoreExceptions, check)
}

class TimeTestUtils {

    private val logger = LoggerFactory.getLogger(TimeTestUtils::class.java)

    @ExperimentalTime
    fun waitUntil(
        message: String,
        initial: Duration?,
        interval: Duration,
        timeout: Duration,
        ignoreExceptions: Boolean,
        check: () -> Boolean
    ) {
        runBlocking {
            val timeoutMs = timeout.inWholeMilliseconds
            val start = System.currentTimeMillis()
            // Logging
            log(message, "Starting...")
            // Waiting some initial time
            if (initial != null) {
                log(message, "Initial delay ($initial")
                delay(initial.inWholeMilliseconds)
            }
            // Checks
            while ((System.currentTimeMillis() - start) < timeoutMs) {
                // Check
                log(message, "Checking...")
                val ok = try {
                    check()
                } catch (ex: Exception) {
                    if (ignoreExceptions) {
                        false // We don't exit because of the exception, but we still need to carry on
                    } else {
                        throw ex
                    }
                }
                if (ok) {
                    // OK
                    log(message, "OK.")
                    return@runBlocking
                } else {
                    log(message, "Interval delay ($interval")
                    delay(interval.inWholeMilliseconds)
                }
            }
            // Timeout
            throw TimeoutException("$message: Timeout exceeded after $timeout")
        }
    }

    private fun log(message: String, info: String) {
        logger.info("$message: $info")
    }
}
