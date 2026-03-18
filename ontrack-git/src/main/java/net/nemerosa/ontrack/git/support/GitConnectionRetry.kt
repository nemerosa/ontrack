package net.nemerosa.ontrack.git.support

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import net.nemerosa.ontrack.common.BaseException
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import java.io.InterruptedIOException
import java.net.SocketException
import java.time.Duration

/**
 * Mechanism to retry Git connections in case of network issues.
 */
object GitConnectionRetry {

    fun <T> retry(
        message: String,
        retries: UInt,
        interval: Duration,
        retryOn5xx: Boolean = true,
        retryOn400: Boolean = false,
        code: () -> T
    ): T {
        var tries = 0u
        var result: T? = null
        while (result == null && tries <= retries) {
            runBlocking {
                try {
                    result = code()
                } catch (any: Exception) {
                    val root = ExceptionUtils.getRootCause(any)
                    val mustRetry = root is SocketException ||
                            root is InterruptedIOException ||
                            (root is HttpServerErrorException && retryOn5xx) ||
                            (root is HttpClientErrorException.BadRequest && retryOn400)
                    if (mustRetry) {
                        tries++
                        GitConnectionMetrics.connectRetry()
                        delay(interval.toMillis())
                    } else {
                        throw any
                    }
                }
            }
        }
        return result ?: onTimeoutException(message, retries, interval)
    }

    private fun onTimeoutException(
        message: String,
        retries: UInt,
        interval: Duration
    ): Nothing {
        GitConnectionMetrics.connectError()
        throw GitConnectionException(message, retries, interval)
    }

    private class GitConnectionException(
        message: String,
        retries: UInt,
        interval: Duration
    ) : BaseException(
        "Git connection timeout after $retries tries every $interval on operation [$message]"
    )

}