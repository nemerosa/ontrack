package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.model.structure.*
import org.junit.Test
import kotlin.test.assertEquals

class BuildLinkDisplayPropertyTest {

    private val buildName = "1"
    private val label = "1.0.0"

    @Test
    fun `No settings, no label, use build name`() {
        test(settings = null, label = null, expected = buildName)
    }

    @Test
    fun `No settings, with label, use build name`() {
        test(settings = null, label = label, expected = buildName)
    }

    @Test
    fun `Settings to false, no label, use build name`() {
        test(settings = false, label = null, expected = buildName)
    }

    @Test
    fun `Settings to false, with label, use build name`() {
        test(settings = false, label = label, expected = buildName)
    }

    @Test
    fun `Settings to true, no label, use build name`() {
        test(settings = true, label = null, expected = buildName)
    }

    @Test
    fun `Settings to true, with label, use label`() {
        test(settings = true, label = label, expected = label)
    }

    private fun test(
            settings: Boolean?,
            label: String?,
            expected: String
    ) {
        val settingsProperty = settings?.let { BuildLinkDisplayProperty(it) }
        val labelProperty = label?.let { ReleaseProperty(label) }
        val build = Build.of(
                Branch.of(
                        Project.of(NameDescription.nd("P", "")).withId(ID.of(1)),
                        NameDescription.nd("master", "")
                ).withId(ID.of(10)),
                NameDescription.nd(buildName, ""),
                Signature.of("test")
        )
        assertEquals(
                expected,
                settingsProperty.getLabel(build, labelProperty)
        )
    }

}