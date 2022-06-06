package net.nemerosa.ontrack.common

import kotlinx.coroutines.delay
import java.time.Duration
import java.util.concurrent.TimeoutException

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
