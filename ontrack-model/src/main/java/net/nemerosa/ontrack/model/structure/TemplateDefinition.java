package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;
import net.nemerosa.ontrack.model.form.NamedEntries;

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

    public Form form() {
        return createForm()
                .fill("parameters", parameters)
                ;
    }

    public static Form createForm() {
        return Form.create()
                .with(
                        NamedEntries.of("parameters")
                                .label("Parameters")
                                .help("List of parameters that define the template")
                )
                ;
    }
}
