package net.nemerosa.ontrack.extension.sonarqube.property

import com.nhaarman.mockitokotlin2.whenever
import net.nemerosa.ontrack.extension.sonarqube.SonarQubeExtensionFeature
import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfiguration
import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfigurationService
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.toJson
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import kotlin.test.assertEquals

class SonarQubePropertyTypeTest {

    private val configurationService = mock(SonarQubeConfigurationService::class.java)
    private val configuration = SonarQubeConfiguration(
            "conf-name",
            "https://sonarqube.nemerosa.net",
            "xxxx"
    )

    private val type = SonarQubePropertyType(
            SonarQubeExtensionFeature(),
            configurationService
    )

    @Before
    fun setup() {
        whenever(configurationService.getConfiguration("conf-name")).thenReturn(configuration)
    }

    @Test
    fun clientParsing() {
        val client = mapOf(
                "configuration" to "conf-name",
                "key" to "project:key",
                "validationStamp" to "sonarqube",
                "measures" to listOf("measure-1", "measure-2"),
                "override" to true
        ).asJson()
        val parsed = type.fromClient(client)
        assertEquals("conf-name", parsed.configuration.name)
        assertEquals("https://sonarqube.nemerosa.net", parsed.configuration.url)
        assertEquals("xxxx", parsed.configuration.password)
        assertEquals("project:key", parsed.key)
        assertEquals("sonarqube", parsed.validationStamp)
        assertEquals(listOf("measure-1", "measure-2"), parsed.measures)
        assertEquals(true, parsed.override)
    }

    @Test
    fun clientParsingWithNoMeasure() {
        val client = mapOf(
                "configuration" to "conf-name",
                "key" to "project:key",
                "validationStamp" to "sonarqube",
                "measures" to listOf<String>(),
                "override" to false
        ).toJson()!!
        val parsed = type.fromClient(client)
        assertEquals("conf-name", parsed.configuration.name)
        assertEquals("https://sonarqube.nemerosa.net", parsed.configuration.url)
        assertEquals("xxxx", parsed.configuration.password)
        assertEquals("project:key", parsed.key)
        assertEquals("sonarqube", parsed.validationStamp)
        assertEquals(emptyList(), parsed.measures)
        assertEquals(false, parsed.override)
    }

    @Test
    fun storage() {
        val item = SonarQubeProperty(
                configuration = configuration,
                key = "project:key",
                validationStamp = "sonarqube",
                measures = listOf("measure-1", "measure-2"),
                override = false,
                branchPattern = null
        )
        // For storage
        val stored = type.forStorage(item)
        // From storage
        val retrieved = type.fromStorage(stored)
        // Checks
        assertEquals("conf-name", retrieved.configuration.name)
        assertEquals("https://sonarqube.nemerosa.net", retrieved.configuration.url)
        assertEquals("xxxx", retrieved.configuration.password)
        assertEquals("project:key", retrieved.key)
        assertEquals("sonarqube", retrieved.validationStamp)
        assertEquals(listOf("measure-1", "measure-2"), retrieved.measures)
        assertEquals(false, retrieved.override)
    }

    @Test
    fun storageWithNoMeasure() {
        val item = SonarQubeProperty(
                configuration = configuration,
                key = "project:key",
                validationStamp = "sonarqube",
                measures = emptyList(),
                override = false,
                branchPattern = null
        )
        // For storage
        val stored = type.forStorage(item)
        // From storage
        val retrieved = type.fromStorage(stored)
        // Checks
        assertEquals("conf-name", retrieved.configuration.name)
        assertEquals("https://sonarqube.nemerosa.net", retrieved.configuration.url)
        assertEquals("xxxx", retrieved.configuration.password)
        assertEquals("project:key", retrieved.key)
        assertEquals("sonarqube", retrieved.validationStamp)
        assertEquals(emptyList(), retrieved.measures)
        assertEquals(false, retrieved.override)
    }

}