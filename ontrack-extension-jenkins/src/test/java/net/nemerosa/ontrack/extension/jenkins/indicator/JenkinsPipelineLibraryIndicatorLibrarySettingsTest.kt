package net.nemerosa.ontrack.extension.jenkins.indicator

import net.nemerosa.ontrack.common.Version.Companion.parseVersion
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class JenkinsPipelineLibraryIndicatorLibrarySettingsTest {

    @Test
    fun `Compliance for null versions`() {
        assertEquals(100, settings(required = false).complianceAsInt(null))
        assertEquals(0, settings(required = true).complianceAsInt(null))
    }

    @Test
    fun `Compliance with no level`() {
        assertEquals(100, settings().complianceAsInt(parseVersion("1.0.0")))
    }

    @Test
    fun `Compliance with one level`() {
        val settings = settings(lastSupported = "5")
        assertEquals(0, settings.complianceAsInt(parseVersion("1.0.0")))
        assertEquals(0, settings.complianceAsInt(parseVersion("2.0.0")))
        assertEquals(0, settings.complianceAsInt(parseVersion("3.0.0")))
        assertEquals(0, settings.complianceAsInt(parseVersion("4.0.0")))
        assertEquals(100, settings.complianceAsInt(parseVersion("5.0.0")))
        assertEquals(100, settings.complianceAsInt(parseVersion("6.0.0")))
    }

    @Test
    fun `Compliance with two levels`() {
        val settings = settings(
            lastUnsupported = "3",
            lastSupported = "5"
        )
        assertEquals(0, settings.complianceAsInt(parseVersion("1.0.0")))
        assertEquals(0, settings.complianceAsInt(parseVersion("2.0.0")))
        assertEquals(50, settings.complianceAsInt(parseVersion("3.0.0")))
        assertEquals(50, settings.complianceAsInt(parseVersion("4.0.0")))
        assertEquals(100, settings.complianceAsInt(parseVersion("5.0.0")))
        assertEquals(100, settings.complianceAsInt(parseVersion("6.0.0")))
    }

    @Test
    fun `Compliance with three levels`() {
        val settings = settings(
            lastUnsupported = "3",
            lastDeprecated = "4",
            lastSupported = "5"
        )
        assertEquals(0, settings.complianceAsInt(parseVersion("1.0.0")))
        assertEquals(0, settings.complianceAsInt(parseVersion("2.0.0")))
        assertEquals(33, settings.complianceAsInt(parseVersion("3.0.0")))
        assertEquals(66, settings.complianceAsInt(parseVersion("4.0.0")))
        assertEquals(100, settings.complianceAsInt(parseVersion("5.0.0")))
        assertEquals(100, settings.complianceAsInt(parseVersion("6.0.0")))
    }

    private fun settings(
        required: Boolean = true,
        lastSupported: String? = null,
        lastDeprecated: String? = null,
        lastUnsupported: String? = null,
    ) = JenkinsPipelineLibraryIndicatorLibrarySettings(
        library = uid("lib-"),
        required = required,
        lastSupported = parseVersion(lastSupported),
        lastDeprecated = parseVersion(lastDeprecated),
        lastUnsupported = parseVersion(lastUnsupported),
    )

}