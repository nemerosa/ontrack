package net.nemerosa.ontrack.model.buildfilter;

import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;

@Data
public class BuildFilterForm {

    private final Class<? extends BuildFilterProvider> type;
    private final String typeName;
    private final boolean predefined;
    private final Form form;

}
