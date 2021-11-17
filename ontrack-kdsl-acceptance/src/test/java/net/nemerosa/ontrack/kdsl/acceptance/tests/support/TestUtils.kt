package net.nemerosa.ontrack.kdsl.acceptance.tests.support

import com.fasterxml.jackson.databind.JsonNode
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import net.nemerosa.ontrack.json.parseAsJson
import org.apache.commons.io.IOUtils
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicLong

/**
 * Sequence for unique IDs.
 */
private val counter = AtomicLong()

/**
 * Generates a unique ID.
 */
fun uid(prefix: String): String =
    prefix + SimpleDateFormat("mmssSSS").format(Date()) + counter.incrementAndGet()

/**
 * Waiting until a condition is met.
 */
fun waitUntil(
    timeout: Long = 60_000L,
    interval: Long = 10_000L,
    condition: () -> Boolean,
) {
    runBlocking {
        withTimeout(timeout) {
            while (!condition()) {
                delay(interval)
            }
        }
    }
}

/**
 * Reads a resource from the classpath as JSON
 */
fun resourceAsJson(path: String): JsonNode =
    resourceAsText(path).parseAsJson()

/**
 * Reads a resource from the classpath as text
 */
fun resourceAsText(path: String, charset: Charset = Charsets.UTF_8): String =
    resourceAsBytes(path).toString(charset)


/**
 * Reads a resource from the classpath as binary
 */
fun resourceAsBytes(path: String): ByteArray = IOUtils.toByteArray(TestUtils::class.java.getResource(path))

/**
 * Anchor class for the resource paths
 */
private class TestUtils
