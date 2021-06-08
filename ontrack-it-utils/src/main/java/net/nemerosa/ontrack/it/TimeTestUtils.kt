package net.nemerosa.ontrack.it

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import kotlin.time.Duration
import kotlin.time.ExperimentalTime
import kotlin.time.seconds

/**
 * Waits until a given condition is met.
 *
 * @param message Activity being waited for
 * @param timings Time to wait (initial timing, interval, max retries)
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
            val timeout = timeout.toLongMilliseconds()
            val start = System.currentTimeMillis()
            // Logging
            log(message, "Starting...")
            // Waiting some initial time
            if (initial != null) {
                log(message, "Initial delay ($initial")
                delay(initial.toLongMilliseconds())
            }
            // Checks
            while ((System.currentTimeMillis() - start) < timeout) {
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
                    delay(interval.toLongMilliseconds())
                }
            }
            // Timeout
            throw IllegalStateException("$message: Timeout exceeded after $timeout")
        }
    }

    private fun log(message: String, info: String) {
        logger.info("$message: $info")
    }
}
