package net.nemerosa.ontrack.extension.av.properties.yaml

import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import net.nemerosa.ontrack.extension.av.properties.support.JsonPropertyAccessor
import net.nemerosa.ontrack.test.TestUtils
import org.junit.Test
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Testing reading and writing YAML files
 */
class YamlIOTest {

    @Test
    fun `Reading and writing as string`() {
        val yaml = Yaml()
        // Content as string
        val content = TestUtils.resourceString("/samples/yaml/complex.yml")
        // Parsing of the string
        val json: List<ObjectNode> = yaml.read(content)
        // Getting a value
        val value = json[4]["spec"]["template"]["spec"]["containers"][0]["image"]
        assertEquals("docker-delivery.repository.sample.io/test/listener:0.1.1", value.asText())
        // Setting a value
        (json[4]["spec"]["template"]["spec"]["containers"][0] as ObjectNode).set<ObjectNode>(
            "image",
            TextNode("docker-delivery.repository.sample.io/test/listener:0.2.0")
        )
        // Writing as string
        val newContent = yaml.write(json)
        assertTrue("docker-delivery.repository.sample.io/test/listener:0.2.0" in newContent)
    }

    @Test
    fun `Spring EL applied to YAML`() {
        val yaml = Yaml()
        // Content as string
        val content = TestUtils.resourceString("/samples/yaml/complex.yml")
        // Parsing of the string
        val json: List<ObjectNode> = yaml.read(content)
        // Spring EL setup
        val parser = SpelExpressionParser()
        val context = StandardEvaluationContext().apply {
            addPropertyAccessor(JsonPropertyAccessor())
        }
        // Expression
        val x =
            "#root.^[kind == 'Deployment' and metadata.name == 'test-listener'].spec.template.spec.containers.^[name == 'listener-cnt'].image"
        val expression = parser.parseExpression(x)
        // Reading
        val value = expression.getValue(context, json)
        assertEquals("docker-delivery.repository.sample.io/test/listener:0.1.1", value)
        // Writing
        expression.setValue(context, json, "docker-delivery.repository.sample.io/test/listener:0.2.0")
        // Writing as string
        val newContent = yaml.write(json)
        assertTrue("docker-delivery.repository.sample.io/text/listener:0.2.0" in newContent)
    }
}

