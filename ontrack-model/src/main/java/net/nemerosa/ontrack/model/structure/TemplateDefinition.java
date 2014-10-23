package net.nemerosa.ontrack.model.structure;

import com.google.common.collect.Maps;
import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Describes the definition of a branch template.
 */
@Data
public class TemplateDefinition {

    /**
     * List of template parameters for this definition.
     */
    private final List<TemplateParameter> parameters;

    /**
     * Source of branch names
     *
     * @see TemplateSynchronisationSource#getId()
     */
    private final ServiceConfiguration synchronisationSourceConfig;

    /**
     * Policy to apply when a branch is configured but no longer available.
     */
    private final TemplateSynchronisationAbsencePolicy absencePolicy;

    /**
     * Synchronisation interval (in minutes). 0 means that synchronisation must be performed manually.
     */
    private final int interval;

    /**
     * Gets the execution context for the creation of a template instance.
     *
     * @param branchName       Input for the expression
     * @param expressionEngine Expression engine to use
     * @return Transformed string
     */
    public TemplateInstanceExecution templateInstanceExecution(String branchName, ExpressionEngine expressionEngine) {
        // Transforms each parameter in a name/value pair, using only the branch name as input
        Map<String, String> branchNameInput = Collections.singletonMap("branchName", branchName);
        Map<String, String> parameterMap = Maps.transformValues(
                Maps.uniqueIndex(
                        parameters,
                        TemplateParameter::getName
                ),
                parameter -> expressionEngine.render(parameter.getExpression(), branchNameInput)
        );
        // Concatenates the maps
        Map<String, String> inputMap = new HashMap<>(branchNameInput);
        inputMap.putAll(parameterMap);
        // Resolves the final expression
        return new TemplateInstanceExecution(
                value -> expressionEngine.render(value, inputMap),
                parameterMap
        );
    }

    /**
     * Checks the compilation of the parameters.
     *
     * @param expressionEngine Engine to use for the compilation
     * @throws net.nemerosa.ontrack.model.exceptions.ExpressionCompilationException In case of compilation problem
     * @see net.nemerosa.ontrack.model.structure.ExpressionEngine#render(String, java.util.Map)
     */
    public void checkCompilation(ExpressionEngine expressionEngine) {
        templateInstanceExecution("x", expressionEngine);
    }
}
