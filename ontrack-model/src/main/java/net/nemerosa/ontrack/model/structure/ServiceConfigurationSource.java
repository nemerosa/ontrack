package net.nemerosa.ontrack.model.structure;

import lombok.Data;
import net.nemerosa.ontrack.model.form.Form;

@Data
public class ServiceConfigurationSource {

    private final String id;
    private final String name;
    private final Form form;

}
