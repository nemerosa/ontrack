package net.nemerosa.ontrack.model.structure;

import lombok.Data;

/**
 * Value for a template parameter.
 */
@Data
public class TemplateParameterValue {

    /**
     * Name of the parameter.
     */
    private final String name;
    /**
     * Value of the parameter.
     */
    private final String value;

}
