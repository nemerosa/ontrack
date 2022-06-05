package net.nemerosa.ontrack.extension.av.properties.yaml

import com.fasterxml.jackson.databind.node.ObjectNode
import net.nemerosa.ontrack.extension.av.properties.support.JsonPropertyAccessor
import org.springframework.expression.EvaluationContext
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.expression.spel.support.StandardEvaluationContext

class YamlAccessor(
        content: String
) {

    private val yaml = Yaml()
    private val root: List<ObjectNode>
    private val context: EvaluationContext
    private val parser: SpelExpressionParser

    init {
        // Parsing of the string
        root = yaml.read(content)
        // Spring EL setup
        parser = SpelExpressionParser()
        context = StandardEvaluationContext().apply {
            addPropertyAccessor(JsonPropertyAccessor())
        }
    }

    fun getValue(spel: String): Any? = parser.parseExpression(spel).getValue(context, root)

    fun setValue(spel: String, value: Any) {
        parser.parseExpression(spel).setValue(context, root, value)
    }

    fun write(): String = yaml.write(root)
}