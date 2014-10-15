package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.List;

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

}
