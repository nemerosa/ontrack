package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;

@Data
public class BuildFilterForm {

    private final Class<? extends BuildFilterProvider> type;
    private final Form form;

}
