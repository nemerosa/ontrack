package net.nemerosa.ontrack.git.support

import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException
import java.io.InterruptedIOException
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class GitConnectionRetryTest {

    @Test
    fun `Direct result`() {
        val result = GitConnectionRetry.retry(
            message = "test",
            config = GitConnectionConfig.default,
            defaultRetries = 10u,
            defaultInterval = Duration.ofMillis(100),
        ) {
            1
        }
        assertEquals(1, result)
    }

    @Test
    fun `Result after some connection issues`() {
        var count = 0
        val result = GitConnectionRetry.retry(
            message = "test",
            config = GitConnectionConfig.default,
            defaultRetries = 10u,
            defaultInterval = Duration.ofMillis(100),
        ) {
            count++
            if (count <= 5) {
                throw InterruptedIOException("test")
            } else {
                count
            }
        }
        assertEquals(6, result)
    }

    @Test
    fun `Not found exception not filtered out`() {
        var count = 0
        assertFailsWith<NotFoundException> {
            GitConnectionRetry.retry(
                message = "test",
                config = GitConnectionConfig.default,
                defaultRetries = 10u,
                defaultInterval = Duration.ofMillis(100),
            ) {
                count++
                if (count <= 5) {
                    throw NotFoundException()
                } else {
                    count
                }
            }
        }
    }

    @Test
    fun `Retry on 5xx by default`() {
        var count = 0
        val result = GitConnectionRetry.retry(
            message = "test",
            config = GitConnectionConfig.default,
            defaultRetries = 10u,
            defaultInterval = Duration.ofMillis(100),
        ) {
            count++
            if (count <= 5) {
                throw HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "test")
            } else {
                count
            }
        }
        assertEquals(6, result)
    }

    @Test
    fun `No retry on 5xx if disabled`() {
        var count = 0
        assertFailsWith<HttpServerErrorException> {
            GitConnectionRetry.retry(
                message = "test",
                defaultRetries = 10u,
                defaultInterval = Duration.ofMillis(100),
                config = GitConnectionConfig(
                    retries = emptyList(),
                ),
            ) {
                count++
                if (count <= 5) {
                    throw HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "test")
                }
            }
        }
    }

    @Test
    fun `No retry on 400 by default`() {
        var count = 0
        assertFailsWith<HttpClientErrorException.BadRequest> {
            GitConnectionRetry.retry(
                message = "test",
                defaultRetries = 10u,
                defaultInterval = Duration.ofMillis(100),
                config = GitConnectionConfig.default,
            ) {
                count++
                if (count <= 5) {
                    throw HttpClientErrorException.BadRequest.create("test", HttpStatus.BAD_REQUEST, "test", HttpHeaders(), ByteArray(0), null)
                }
            }
        }
    }

    @Test
    fun `Retry on 400 if enabled`() {
        var count = 0
        val result = GitConnectionRetry.retry(
            message = "test",
            defaultRetries = 10u,
            defaultInterval = Duration.ofMillis(100),
            config = GitConnectionConfig(
                retries = listOf(
                    GitConnectionRetryConfig(
                        httpCode = "400",
                    )
                ),
            ),
        ) {
            count++
            if (count <= 5) {
                throw HttpClientErrorException.BadRequest.create("test", HttpStatus.BAD_REQUEST, "test", HttpHeaders(), ByteArray(0), null)
            } else {
                count
            }
        }
        assertEquals(6, result)
    }

    @Test
    fun `Retry with GitConnectionConfig`() {
        var count = 0
        val config = GitConnectionConfig(
            retries = listOf(
                GitConnectionRetryConfig(
                    httpCode = "500",
                    retryLimit = 3,
                    retryInterval = Duration.ofMillis(10)
                )
            )
        )
        val result = GitConnectionRetry.retry(
            message = "test",
            config = config,
            defaultRetries = 10u,
            defaultInterval = Duration.ofMillis(100),
        ) {
            count++
            if (count <= 3) {
                throw HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "test")
            } else {
                count
            }
        }
        assertEquals(4, result)
    }

    @Test
    fun `Retry with GitConnectionConfig and default fallback`() {
        var count = 0
        val config = GitConnectionConfig(
            retries = listOf(
                GitConnectionRetryConfig(
                    httpCode = "500",
                    // using defaults for limit and interval
                )
            )
        )
        val result = GitConnectionRetry.retry(
            message = "test",
            config = config,
            defaultRetries = 5u,
            defaultInterval = Duration.ofMillis(10),
        ) {
            count++
            if (count <= 5) {
                throw HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "test")
            } else {
                count
            }
        }
        assertEquals(6, result)
    }

    @Test
    fun `No retry if no match in GitConnectionConfig`() {
        var count = 0
        val config = GitConnectionConfig(
            retries = listOf(
                GitConnectionRetryConfig(
                    httpCode = "503"
                )
            )
        )
        assertFailsWith<HttpServerErrorException> {
            GitConnectionRetry.retry(
                message = "test",
                config = config,
                defaultRetries = 10u,
                defaultInterval = Duration.ofMillis(100),
            ) {
                count++
                throw HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "test")
            }
        }
        assertEquals(1, count)
    }

    private class NotFoundException : RuntimeException()

}