package net.nemerosa.ontrack.model.structure;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;

/**
 * Explicit association between a branch and its template definition.
 */
@Data
public class LoadedBranchTemplateDefinition {

    private final Branch branch;
    private final TemplateDefinition templateDefinition;

    @JsonIgnore
    public Form getForm() {
        return templateDefinition.getForm();
    }
}
