package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.util.Map;

/**
 * Synchronisation parameters for a {@linkplain net.nemerosa.ontrack.model.structure.TemplateDefinition template definition}.
 */
@Data
public class TemplateSynchronisation {

    /**
     * TODO Source of branch names
     */

    /**
     * Synchronisation interval (in minutes). 0 means that synchronisation must be performed manually.
     */
    private final int interval;

    /**
     * Template expressions.
     */
    private final Map<String, String> templateExpressions;

}
