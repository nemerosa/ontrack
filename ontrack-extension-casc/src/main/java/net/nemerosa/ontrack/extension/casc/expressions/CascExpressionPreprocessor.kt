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
            val value = processExpression(expression)
            // If the replacement is on multiple lines,
            // we need to indent the result accordingly,
            // by taking the same amount of white spaces
            // as before the match
            val valueLines = value.lines()
            if (valueLines.size > 1) {
                val prefix = line.substring(0, m.range.first)
                if (prefix.isBlank()) {
                    // Indenting all lines but the first one
                    valueLines.mapIndexed { index, s ->
                        if (index > 0) {
                            prefix + s
                        } else {
                            s
                        }
                    }.joinToString("\n")
                }
                // Not empty in front of the expression, skipping any indentation
                else {
                    value
                }
            }
            // No multiline --> inline
            else {
                value
            }
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