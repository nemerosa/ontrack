package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Definition of a branch instance
 */
@Data
public class TemplateInstance {

    /**
     * Template definition (branch ID)
     */
    private final ID templateDefinitionId;

    /**
     * List of parameter values
     */
    private final List<TemplateParameterValue> parameterValues;

    @JsonIgnore
    public Map<String, String> getParameterMap() {
        return parameterValues.stream().collect(Collectors.toMap(
                TemplateParameterValue::getName,
                TemplateParameterValue::getValue
        ));
    }
}
