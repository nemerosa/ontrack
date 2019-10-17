package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Maps;
import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.Text;
import net.nemerosa.ontrack.model.form.YesNo;

import javax.validation.constraints.NotNull;
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
    @NotNull
    private final ServiceConfiguration synchronisationSourceConfig;

    /**
     * Policy to apply when a branch is configured but no longer available.
     */
    @NotNull
    private final TemplateSynchronisationAbsencePolicy absencePolicy;

    /**
     * Synchronisation interval (in minutes). 0 means that synchronisation must be performed manually.
     */
    private final int interval;

    /**
     * Gets the execution context for the creation of a template instance.
     *
     * @param sourceName       Input for the expression
     * @param expressionEngine Expression engine to use
     * @return Transformed string
     */
    public TemplateInstanceExecution templateInstanceExecution(String sourceName, ExpressionEngine expressionEngine) {
        // Transforms each parameter in a name/value pair, using only the source name as input
        Map<String, String> sourceNameInput = Collections.singletonMap("sourceName", sourceName);
        Map<String, String> parameterMap = Maps.transformValues(
                Maps.uniqueIndex(
                        parameters,
                        TemplateParameter::getName
                ),
                parameter -> expressionEngine.render(parameter.getExpression(), sourceNameInput)
        );
        // Concatenates the maps
        Map<String, String> inputMap = new HashMap<>(sourceNameInput);
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

    @JsonIgnore
    public Form getForm() {
        Form form = Form.create();
        // Parameters only if at least one is available
        if (!parameters.isEmpty()) {
            // Auto expression
            form = form.with(
                    YesNo.of("manual")
                            .label("Manual")
                            .help("Do not use automatic expansion of parameters using the branch name.")
                            .value(false)
            );
            // Template parameters
            for (TemplateParameter parameter : parameters) {
                form = form.with(
                        Text.of(parameter.getName())
                                .label(parameter.getName())
                                .visibleIf("manual")
                                .help(parameter.getDescription())
                );
            }
        }
        // OK
        return form;
    }
}
