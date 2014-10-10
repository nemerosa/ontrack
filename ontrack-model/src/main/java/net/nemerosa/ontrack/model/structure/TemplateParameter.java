package net.nemerosa.ontrack.model.structure;

import lombok.Data;

/**
 * Definition of a template parameter.
 */
@Data
public class TemplateParameter {

    /**
     * Name of the parameter, used for template expressions.
     */
    private final String name;
    /**
     * Description of the parameter.
     */
    private final String description;

}
