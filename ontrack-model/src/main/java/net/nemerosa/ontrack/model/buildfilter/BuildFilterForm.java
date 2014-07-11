package net.nemerosa.ontrack.model.buildfilter;

import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;

import java.util.List;
import java.util.Map;

@Data
public class BuildFilterForm {

    private final Class<? extends BuildFilterProvider> type;
    private final String typeName;
    private final boolean predefined;
    private final Form form;

    public BuildFilterForm with(Map<String, String> data) {
        return new BuildFilterForm(
                type,
                typeName,
                predefined,
                form.fill(data)
        );
    }
}
