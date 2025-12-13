package net.nemerosa.ontrack.common

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.concurrent.TimeoutException
import kotlin.time.ExperimentalTime

suspend fun <T> untilTimeout(
    name: String,
    timeout: Duration,
    retryDelay: Duration = Duration.ofSeconds(30),
    logger: (String) -> Unit = {},
    code: () -> T?,
): T {
    val retries = (timeout.toMillis() / retryDelay.toMillis()) + 1
    return untilTimeout(
        name = name,
        retryCount = retries.toInt(),
        retryDelay = retryDelay,
        logger = logger,
        code = code,
    )
}

/**
 * Runs some [code] until it returns something (different than null) and does not timeout.
 *
 * @param name Name of the operation (used for logging)
 * @param retryCount Number of tries (defaults to 10)
 * @param retryDelay Interval between each try (defaults to 30 seconds)
 * @param logger Logging of operation (defaults to NOP)
 * @param code Code to run at each try. Must return `null` if the operation is not finished, not `null` otherwise.
 * @throws TimeoutException If the operation times out
 */
suspend fun <T> untilTimeout(
    name: String,
    retryCount: Int = 10,
    retryDelay: Duration = Duration.ofSeconds(30),
    logger: (String) -> Unit = {},
    code: () -> T?,
): T {
    var tries = 1
    while (tries <= retryCount) {
        logger("$tries/$retryCount - $name")
        val result: T? = try {
            code()
        } catch (ex: Exception) {
            logger("$tries/$retryCount - $name - exception: ${ex.message}")
            null
        }
        if (result != null) {
            logger("$tries/$retryCount - $name - result OK")
            return result
        } else {
            logger("$tries/$retryCount - $name - no result yet, keeping waiting")
            delay(retryDelay.toMillis())
            tries++
        }
    }
    // Timout
    logger("$tries/$retryCount - $name - timeout")
    throw TimeoutException("$name - Could not get result in time")
}


@OptIn(ExperimentalTime::class)
fun <T> waitFor(
    message: String,
    initial: kotlin.time.Duration? = null,
    interval: kotlin.time.Duration = 5.seconds,
    timeout: kotlin.time.Duration = 60.seconds,
    code: () -> T?
): Waiting<T> = Waiting(
    message = message,
    initial = initial,
    interval = interval,
    timeout = timeout,
    access = code,
)

@OptIn(ExperimentalTime::class)
class Waiting<T>(
    private val message: String,
    private val initial: kotlin.time.Duration? = null,
    private val interval: kotlin.time.Duration = 5.seconds,
    private val timeout: kotlin.time.Duration = 60.seconds,
    private val access: () -> T?
) {

    private val logger = LoggerFactory.getLogger(Waiting::class.java)

    infix fun until(check: (t: T) -> Boolean): T {
        return runBlocking {
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
                // Getting the input
                val t = access()
                if (t != null) {
                    val ok = check(t)
                    if (ok) {
                        // OK
                        log(message, "OK.")
                        return@runBlocking t
                    }
                }
                log(message, "Interval delay ($interval")
                delay(interval.inWholeMilliseconds)
            }
            // Timeout
            throw TimeoutException("$message: Timeout exceeded after $timeout")
        }
    }

    private fun log(message: String, info: String) {
        logger.info("$message: $info")
    }

}