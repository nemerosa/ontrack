package net.nemerosa.ontrack.model.structure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;

import java.util.Collections;
import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ServiceConfigurationSource {

    private final String id;
    private final String name;
    private final Form form;

    /**
     * Specific data for a source (optional).
     */
    private final Map<String, ?> extra;

    public ServiceConfigurationSource(String id, String name, Form form) {
        this(id, name, form, Collections.emptyMap());
    }


}
