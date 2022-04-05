package net.nemerosa.ontrack.extension.casc.expressions

import net.nemerosa.ontrack.extension.casc.CascPreprocessor
import org.springframework.stereotype.Component

@Component
class CascExpressionPreprocessor(
    expressionContexts: List<CascExpressionContext>,
) : CascPreprocessor {

    private val contexts = expressionContexts.associateBy { it.name }

    override fun process(yaml: String): String =
        yaml.lines().joinToString("\n") { processLine(it) }

    private fun processLine(line: String): String =
        line.replace(pattern) { m ->
            val expression = m.groupValues[1].trim()
            processExpression(expression)
        }

    private fun processExpression(expression: String): String {
        val name = expression.substringBefore(".")
        val rest = expression.substringAfter(".")
        if (name.isBlank()) {
            throw CascExpressionMissingNameException()
        }
        val context = contexts[name] ?: throw CascExpressionUnknownException(name)
        return context.evaluate(rest)
    }

    companion object {
        private val pattern = "\\{\\{([^}]+)}}".toRegex()
    }
}