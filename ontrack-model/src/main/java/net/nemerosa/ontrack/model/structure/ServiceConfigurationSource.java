package net.nemerosa.ontrack.model.structure;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.Map;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ServiceConfigurationSource {

    private final String id;
    private final String name;

    /**
     * Specific data for a source (optional).
     */
    private final Map<String, ?> extra;

    public ServiceConfigurationSource(String id, String name) {
        this(id, name, Collections.emptyMap());
    }


}
