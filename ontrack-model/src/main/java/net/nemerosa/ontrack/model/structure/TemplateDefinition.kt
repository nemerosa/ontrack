package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonIgnore
import net.nemerosa.ontrack.model.form.Form
import net.nemerosa.ontrack.model.form.Text
import net.nemerosa.ontrack.model.form.YesNo
import java.util.*

/**
 * Describes the definition of a branch template.
 *
 * @property parameters List of template parameters for this definition.
 * @property synchronisationSourceConfig Source of branch names
 * @property absencePolicy Policy to apply when a branch is configured but no longer available.
 * @property interval Synchronisation interval (in minutes). 0 means that synchronisation must be performed manually.
 */
data class TemplateDefinition(
        val parameters: List<TemplateParameter>,
        val synchronisationSourceConfig: ServiceConfiguration,
        val absencePolicy: TemplateSynchronisationAbsencePolicy,
        val interval: Int
) {

    // Parameters only if at least one is available
    // Auto expression
    // Template parameters
    // OK
    val form: Form
        @JsonIgnore
        get() {
            var form = Form.create()
            if (parameters.isNotEmpty()) {
                form = form.with(
                        YesNo.of("manual")
                                .label("Manual")
                                .help("Do not use automatic expansion of parameters using the branch name.")
                                .value(false)
                )
                for ((name, description) in parameters) {
                    form = form.with(
                            Text.of(name)
                                    .label(name)
                                    .visibleIf("manual")
                                    .help(description)
                    )
                }
            }
            return form
        }

    /**
     * Gets the execution context for the creation of a template instance.
     *
     * @param sourceName       Input for the expression
     * @param expressionEngine Expression engine to use
     * @return Transformed string
     */
    fun templateInstanceExecution(sourceName: String, expressionEngine: ExpressionEngine): TemplateInstanceExecution {
        // Transforms each parameter in a name/value pair, using only the source name as input
        val sourceNameInput = Collections.singletonMap("sourceName", sourceName)
        val parameterMap = parameters.associateBy {
            it.name
        }.mapValues { (_, parameter) ->
            expressionEngine.render(parameter.expression, sourceNameInput)
        }
        // Concatenates the maps
        val inputMap = sourceNameInput.toMutableMap()
        inputMap.putAll(parameterMap)
        // Resolves the final expression
        return TemplateInstanceExecution(
                { value -> expressionEngine.render(value, inputMap) },
                parameterMap
        )
    }

    /**
     * Checks the compilation of the parameters.
     *
     * @param expressionEngine Engine to use for the compilation
     * @throws net.nemerosa.ontrack.model.exceptions.ExpressionCompilationException In case of compilation problem
     * @see net.nemerosa.ontrack.model.structure.ExpressionEngine.render
     */
    fun checkCompilation(expressionEngine: ExpressionEngine) {
        templateInstanceExecution("x", expressionEngine)
    }
}
