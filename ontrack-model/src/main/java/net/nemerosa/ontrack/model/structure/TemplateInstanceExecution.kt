package net.nemerosa.ontrack.model.structure

/**
 * Execution context for the creation of a template instance.
 *
 * @property replacementFn Replacement function to use
 * @property parameterValues List of resolved template definition parameters
 */
class TemplateInstanceExecution(
        val replacementFn: (String) -> String,
        val parameterValues: Map<String, String>
) {

    /**
     * Performs a replacement
     */
    fun replace(value: String): String {
        return replacementFn(value)
    }

    fun asTemplateParameterValues(): List<TemplateParameterValue> {
        return parameterValues.map { (key, value) ->
            TemplateParameterValue(key, value)
        }
    }
}
