package net.nemerosa.ontrack.extension.general

import org.junit.Test
import kotlin.test.assertEquals

class BuildLinkDisplayPropertyTest {

    private val buildName = "1"
    private val release = "1.0.0"

    @Test
    fun `No settings, no release, use build name`() {
        test(settings = null, release = null, expected = null)
    }

    @Test
    fun `No settings, with release, use release name`() {
        test(settings = null, release = release, expected = release)
    }

    @Test
    fun `Settings to false, no release, use build name`() {
        test(settings = false, release = null, expected = null)
    }

    @Test
    fun `Settings to false, with release, use build name`() {
        test(settings = false, release = release, expected = null)
    }

    @Test
    fun `Settings to true, no release, use build name`() {
        test(settings = true, release = null, expected = null)
    }

    @Test
    fun `Settings to true, with release, use release`() {
        test(settings = true, release = release, expected = release)
    }

    private fun test(
        settings: Boolean?,
        release: String?,
        expected: String?
    ) {
        val settingsProperty = settings?.let { BuildLinkDisplayProperty(it) }
        val labelProperty = release?.let { ReleaseProperty(release) }
        assertEquals(
            expected,
            settingsProperty.getLabel(labelProperty)
        )
    }

}