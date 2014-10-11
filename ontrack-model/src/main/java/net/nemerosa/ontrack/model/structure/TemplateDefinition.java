package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.NamedEntries;
import net.nemerosa.ontrack.model.form.Selection;

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
    private final String synchronisationSourceId;

    /**
     * Configuration data for the sync source
     */
    private final JsonNode synchronisationSourceConfig;

    /**
     * Policy to apply when a branch is configured but no longer available.
     */
    private final TemplateSynchronisationAbsencePolicy absencePolicy;

    /**
     * Synchronisation interval (in minutes). 0 means that synchronisation must be performed manually.
     */
    private final int interval;

    /**
     * Template expressions.
     */
    private final Map<String, String> templateExpressions;

    public Form form(TemplateSynchronisationService templateSynchronisationService) {
        return createForm(templateSynchronisationService)
                .fill("parameters", parameters)
                .fill("synchronisationSourceId", synchronisationSourceId)
                ;
    }

    public static Form createForm(TemplateSynchronisationService templateSynchronisationService) {
        return Form.create()
                .with(
                        NamedEntries.of("parameters")
                                .label("Parameters")
                                .help("List of parameters that define the template")
                )
                .with(
                        Selection.of("synchronisationSourceId")
                                .label("Sync. source")
                                .help("Source of branch names when synchronising")
                                .optional()
                                .items(templateSynchronisationService.getSynchronisationSources())
                )
                ;
    }
}
