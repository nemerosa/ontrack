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
}
