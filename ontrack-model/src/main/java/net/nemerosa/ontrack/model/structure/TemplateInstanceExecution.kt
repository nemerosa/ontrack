package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Execution context for the creation of a template instance.
 */
@Data
public class TemplateInstanceExecution {

    /**
     * Replacement function to use
     */
    private final Function<String, String> replacementFn;

    /**
     * List of resolved template definition parameters
     */
    private final Map<String, String> parameterValues;

    /**
     * Performs a replacement
     */
    public String replace(String value) {
        return replacementFn.apply(value);
    }

    public List<TemplateParameterValue> asTemplateParameterValues() {
        return parameterValues.entrySet().stream()
                .map(entry -> new TemplateParameterValue(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}
