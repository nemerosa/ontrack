package net.nemerosa.ontrack.git.support

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.common.BaseException
import org.apache.commons.lang3.exception.ExceptionUtils
import java.time.Duration

/**
 * Mechanism to retry Git connections in case of network issues.
 */
object GitConnectionRetry {

    /**
     * Launching a connection using RestTemplate and retrying in case of network issues.
     *
     * @param message Message to display in the exception
     * @param config Configuration for the retries
     * @param defaultRetries Default number of retries
     * @param defaultInterval Default interval between retries
     * @param code Code to execute
     * @return Result of the code when there is no exception
     */
    fun <T> retry(
        message: String,
        config: GitConnectionConfig,
        defaultRetries: UInt,
        defaultInterval: Duration,
        code: () -> T
    ): T {
        var tries = 0
        var result: T? = null
        var limitReached = false
        val log = mutableListOf<String>()
        while (result == null && !limitReached) {
            runBlocking {
                try {
                    result = code()
                } catch (any: Exception) {
                    val root = ExceptionUtils.getRootCause(any)
                    val retry = config.retries.find { it.match(root) }
                    if (retry != null) {
                        tries++
                        log.add("Retrying $retry because of $root")
                        GitConnectionMetrics.connectRetry()
                        val interval = retry.retryInterval ?: defaultInterval
                        delay(interval.toMillis())
                        val limit = retry.retryLimit ?: defaultRetries.toInt()
                        if (tries > limit) {
                            limitReached = true
                        }
                    } else {
                        log.add("No retry for $root")
                        throw any
                    }
                }
            }
        }
        return result ?: onTimeoutException(message, log)
    }

    private fun onTimeoutException(
        message: String,
        log: List<String>,
    ): Nothing {
        GitConnectionMetrics.connectError()
        throw GitConnectionException(message, log)
    }

    private class GitConnectionException(
        message: String,
        log: List<String>,
    ) : BaseException(
        "Git connection failed on operation [$message], retry log: $log",
    )

}