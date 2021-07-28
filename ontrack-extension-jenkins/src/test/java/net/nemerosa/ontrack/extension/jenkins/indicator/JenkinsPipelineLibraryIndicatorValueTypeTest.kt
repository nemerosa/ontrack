package net.nemerosa.ontrack.extension.jenkins.indicator

import com.fasterxml.jackson.databind.node.IntNode
import com.fasterxml.jackson.databind.node.NullNode
import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.extension.indicators.IndicatorsExtensionFeature
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCompliance
import net.nemerosa.ontrack.extension.jenkins.JenkinsExtensionFeature
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.json.asJson
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class JenkinsPipelineLibraryIndicatorValueTypeTest {

    private val type = JenkinsPipelineLibraryIndicatorValueType(
        JenkinsExtensionFeature(
            IndicatorsExtensionFeature(),
            SCMExtensionFeature()
        )
    )

    @Test
    fun `Form with value`() {
        val form = type.form(config(), version("1.0.0"))
        val field = form.getField("version")
        assertNotNull(field) {
            assertEquals("1.0.0", it.value)
        }
    }

    @Test
    fun `Status when version is not required and without a minimum`() {
        val config = config(false, null)
        assertEquals(IndicatorCompliance.HIGHEST, type.status(config, null))
        assertEquals(IndicatorCompliance.HIGHEST, type.status(config, version("1.0.0")))
        assertEquals(IndicatorCompliance.HIGHEST, type.status(config, version("2.0.0")))
        assertEquals(IndicatorCompliance.HIGHEST, type.status(config, version("3.0.0")))
    }

    @Test
    fun `Status when version is required and without a minimum`() {
        val config = config(true, null)
        assertEquals(IndicatorCompliance.LOWEST, type.status(config, null))
        assertEquals(IndicatorCompliance.HIGHEST, type.status(config, version("1.0.0")))
        assertEquals(IndicatorCompliance.HIGHEST, type.status(config, version("2.0.0")))
        assertEquals(IndicatorCompliance.HIGHEST, type.status(config, version("3.0.0")))
    }

    @Test
    fun `Status when version is not required and with a minimum`() {
        val config = config(false, "2.0.3")
        assertEquals(IndicatorCompliance.HIGHEST, type.status(config, null))
        assertEquals(IndicatorCompliance.MEDIUM, type.status(config, version("1.0.0")))
        assertEquals(IndicatorCompliance.MEDIUM, type.status(config, version("2.0.0")))
        assertEquals(IndicatorCompliance.HIGHEST, type.status(config, version("2.0.3")))
        assertEquals(IndicatorCompliance.HIGHEST, type.status(config, version("3.0.0")))
    }

    @Test
    fun `Status when version is required and with a minimum`() {
        val config = config(true, "2.0.3")
        assertEquals(IndicatorCompliance.LOWEST, type.status(config, null))
        assertEquals(IndicatorCompliance.MEDIUM, type.status(config, version("1.0.0")))
        assertEquals(IndicatorCompliance.MEDIUM, type.status(config, version("2.0.0")))
        assertEquals(IndicatorCompliance.HIGHEST, type.status(config, version("2.0.3")))
        assertEquals(IndicatorCompliance.HIGHEST, type.status(config, version("3.0.0")))
    }

    @Test
    fun `To client JSON`() {
        assertEquals(
            type.toClientJson(config(), version("1.0.0")),
            mapOf("version" to "1.0.0").asJson()
        )
    }

    @Test
    fun `From client JSON`() {
        assertNull(type.fromClientJson(config(), NullNode.instance))
        assertNull(type.fromClientJson(config(), TextNode("1.0.1")))
        assertNull(type.fromClientJson(config(), mapOf("test" to 42).asJson()))
        assertEquals(
            version("1.0.1"),
            type.fromClientJson(config(), mapOf("version" to "1.0.1").asJson())
        )
    }

    @Test
    fun `From stored JSON`() {
        assertNull(type.fromStoredJson(config(), NullNode.instance))
        assertEquals(version("42"), type.fromStoredJson(config(), IntNode(42)))
        assertNull(type.fromStoredJson(config(), mapOf("test" to 42).asJson()))
        assertNull(
            type.fromStoredJson(config(), mapOf("version" to "1.0.1").asJson())
        )
        assertEquals(
            version("1.0.1"),
            type.fromStoredJson(config(), "1.0.1".asJson())
        )
    }

    @Test
    fun `To stored JSON`() {
        assertTrue(type.toStoredJson(config(), null).isNull)
        assertEquals(
            "1.0.1".asJson(),
            type.toStoredJson(config(), version("1.0.1"))
        )
    }

    @Test
    fun `Config form`() {
        val form = type.configForm(config(true, "1.0.1"))
        assertEquals(true, form.getField("versionRequired")?.value)
        assertEquals("1.0.1", form.getField("versionMinimum")?.value)
    }

    @Test
    fun `To config form`() {
        assertEquals(
            mapOf("versionRequired" to true, "versionMinimum" to "1.1.0").asJson(),
            type.toConfigForm(config(true, "1.1.0"))
        )
    }

    @Test
    fun `From config form`() {
        assertEquals(
            config(true, "1.1.0"),
            type.fromConfigForm(mapOf("versionRequired" to true, "versionMinimum" to "1.1.0").asJson())
        )
    }

    @Test
    fun `To config client JSON`() {
        assertEquals(
            mapOf("versionRequired" to true, "versionMinimum" to mapOf("value" to "1.1.0")).asJson(),
            type.toConfigClientJson(config(true, "1.1.0"))
        )
    }

    @Test
    fun `To config stored JSON`() {
        assertEquals(
            mapOf("versionRequired" to true, "versionMinimum" to mapOf("value" to "1.1.0")).asJson(),
            type.toConfigStoredJson(config(true, "1.1.0"))
        )
    }

    @Test
    fun `From config stored JSON`() {
        assertEquals(
            config(true, "1.1.0"),
            type.fromConfigStoredJson(mapOf("versionRequired" to true, "versionMinimum" to mapOf("value" to "1.1.0")).asJson())
        )
    }

    private fun version(value: String) = JenkinsPipelineLibraryVersion(value)

    private fun config(
        versionRequired: Boolean = false,
        versionMinimum: String? = null
    ) =
        JenkinsPipelineLibraryIndicatorValueTypeConfig(
            versionRequired,
            versionMinimum?.let { JenkinsPipelineLibraryVersion(it) }
        )

}