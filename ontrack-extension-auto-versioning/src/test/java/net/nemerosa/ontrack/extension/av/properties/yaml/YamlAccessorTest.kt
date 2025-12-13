package net.nemerosa.ontrack.extension.av.properties.yaml

import net.nemerosa.ontrack.test.TestUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class YamlAccessorTest {

    @Test
    fun `Reading and writing YAML using Spring EL expressions`() {
        // Content as string
        val content = TestUtils.resourceString("/samples/yaml/complex.yml")
        // Accessor on this content
        val accessor = YamlAccessor(content)
        // Expression
        val x =
            "#root.^[kind == 'Deployment' and metadata.name == 'test-listener'].spec.template.spec.containers.^[name == 'listener-cnt'].image"
        // Reading
        val value = accessor.getValue(x)
        assertEquals("docker-delivery.repository.sample.io/test/listener:0.1.1", value)
        // Writing
        accessor.setValue(x, "docker-delivery.repository.sample.io/test/listener:0.2.0")
        // Writing as string
        val newContent = accessor.write()
        assertTrue("docker-delivery.repository.sample.io/test/listener:0.2.0" in newContent)
    }

    @Test
    fun `Reading and writing YAML using Spring EL expressions for a simple map access`() {
        // Content as string
        val content = TestUtils.resourceString("/samples/yaml/map.yaml")
        // Accessor on this content
        val accessor = YamlAccessor(content)
        // Expression
        val x = "#root[0].groups['nemerosa-demo'].version"
        // Reading
        val value = accessor.getValue(x)
        assertEquals("4.3.10", value)
        // Writing
        accessor.setValue(x, "4.3.11")
        // Writing as string
        val newContent = accessor.write()
        assertTrue("""version: "4.3.11"""" in newContent)
    }

    @Test
    fun `Reading YAML property from an empty content`() {
        // Content as string
        val content = ""
        // Accessor on this content
        val accessor = YamlAccessor(content)
        // Expression
        val x = "#root[0].groups['nemerosa-demo'].version"
        // Reading
        assertFailsWith<YamlNoContentException> {
            accessor.getValue(x)
        }
    }

    @Test
    fun `Reading YAML property using an invalid expression`() {
        // Content as string
        val content = TestUtils.resourceString("/samples/yaml/map.yaml")
        // Accessor on this content
        val accessor = YamlAccessor(content)
        // Expression
        val x = "#root[0].groups['nemerosa-demo'].fieldNotFound"
        // Reading
        assertFailsWith<YamlEvaluationException> {
            accessor.getValue(x)
        }
    }

}