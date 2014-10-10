package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.List;

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
     * Synchronisation configuration for this definition.
     */
    private final TemplateSynchronisation synchronisation;

}
