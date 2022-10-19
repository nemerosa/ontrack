package net.nemerosa.ontrack.git.support

import org.junit.jupiter.api.Test
import java.io.InterruptedIOException
import java.time.Duration
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class GitConnectionRetryTest {

    @Test
    fun `Direct result`() {
        val result = GitConnectionRetry.retry(
            message = "test",
            retries = 10u,
            interval = Duration.ofMillis(100),
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
            retries = 10u,
            interval = Duration.ofMillis(100),
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
                retries = 10u,
                interval = Duration.ofMillis(100),
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
    fun `Not found exception with check only for some time`() {
        var count = 0
        val result = GitConnectionRetry.retry(
            message = "test",
            retries = 10u,
            interval = Duration.ofMillis(100),
            exceptionRetryCheck = { it is NotFoundException }
        ) {
            count++
            if (count <= 5) {
                throw NotFoundException()
            } else {
                count
            }
        }
        assertEquals(6, result)
    }

    @Test
    fun `Always not found exception with check`() {
        assertFailsWith<GitConnectionRetry.GitConnectionRetryTimeoutException> {
            GitConnectionRetry.retry(
                message = "test",
                retries = 10u,
                interval = Duration.ofMillis(100),
                exceptionRetryCheck = { it is NotFoundException }
            ) {
                throw NotFoundException()
            }
        }
    }

    private class NotFoundException : RuntimeException()

}