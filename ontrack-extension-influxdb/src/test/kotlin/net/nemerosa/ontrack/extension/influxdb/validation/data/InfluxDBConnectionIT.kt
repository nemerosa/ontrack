package net.nemerosa.ontrack.extension.influxdb.validation.data

import net.nemerosa.ontrack.common.seconds
import net.nemerosa.ontrack.extension.influxdb.DefaultInfluxDBConnection
import net.nemerosa.ontrack.extension.influxdb.InfluxDBConnection
import net.nemerosa.ontrack.extension.influxdb.InfluxDBExtensionProperties
import net.nemerosa.ontrack.it.waitUntil
import net.nemerosa.ontrack.model.security.SecurityService
import org.influxdb.InfluxDB
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Disabled
import org.mockito.Mockito.mock
import org.testcontainers.containers.GenericContainer
import java.time.Duration
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.ExperimentalTime


/**
 * Testing the resilience of the connection to InfluxDB.
 */
@ExperimentalTime
class InfluxDBConnectionIT {

    private lateinit var securityService: SecurityService

    private lateinit var influxDBExtensionProperties: InfluxDBExtensionProperties

    @Before
    fun before() {
        securityService = mock(SecurityService::class.java)
        influxDBExtensionProperties = InfluxDBExtensionProperties().apply {
            enabled = true
            validity = Duration.ofSeconds(5)
        }
    }

    @Test
    fun `Getting a valid connection`() {
        withContainer { influxDBContainer ->
            val connection = influxDBConnection(influxDBContainer)
            assertNotNull(connection.current) { db ->
                assertConnectionOK(db)
            }
            assertTrue(connection.isValid(), "Connection is OK")
        }
    }

    @Test
    fun `A closed connection is marked as invalid`() {
        withContainer { influxDBContainer ->
            val connection = influxDBConnection(influxDBContainer)
            // Testing the connection is no longer valid
            waitUntil("Connection is valid first", interval = 1.seconds, timeout = 6.seconds) {
                connection.isValid()
            }
            // Stopping the container
            influxDBContainer.stop()
            // Testing the connection is no longer valid
            waitUntil("Connection is no longer valid", interval = 1.seconds, timeout = 15.seconds) {
                !connection.isValid()
            }
        }
    }

    @Test
    @Disabled("Flaky")
    fun `A connection will be restored automatically after some time`() {
        withContainer { influxDBContainer ->
            val connection = influxDBConnection(influxDBContainer)
            // Testing the connection is no longer valid
            waitUntil("Connection is valid first", interval = 2.seconds, timeout = 60.seconds) {
                connection.isValid()
            }
            // Stopping the container
            influxDBContainer.stop()
            // Testing the connection is no longer valid
            waitUntil("Connection is no longer valid", interval = 2.seconds, timeout = 60.seconds) {
                !connection.isValid()
            }
            // Starting the container again
            influxDBContainer.start()
            influxDBExtensionProperties.uri = influxDBUri(influxDBContainer)
            // Testing the connection is restored
            waitUntil("Connection is restored", interval = 2.seconds, timeout = 60.seconds) {
                !connection.isValid()
            }
        }
    }

    private fun withContainer(
        code: (influxDBContainer: KGenericContainer) -> Unit
    ) {
        val influxDBContainer: KGenericContainer =
            KGenericContainer("influxdb:1.8.6")
                .withReuse(false)
                .withExposedPorts(8086)
        influxDBContainer.start()
        influxDBContainer.use {
            code(influxDBContainer)
        }
    }

    private fun influxDBConnection(
        influxDBContainer: KGenericContainer
    ): InfluxDBConnection {
        influxDBExtensionProperties.uri = influxDBUri(influxDBContainer)
        return DefaultInfluxDBConnection(
            influxDBExtensionProperties = influxDBExtensionProperties,
            securityService = securityService
        ).apply {
            start()
        }
    }

    private fun influxDBUri(influxDBContainer: KGenericContainer) =
        "http://localhost:${influxDBContainer.getMappedPort(8086)}"

    private fun assertConnectionOK(connection: InfluxDB) {
        val pong = connection.ping()
        assertNotNull(pong) {
            assertTrue(it.isGood, "Connection to InfluxDB is OK")
        }
    }

}

class KGenericContainer(imageName: String) : GenericContainer<KGenericContainer>(imageName)