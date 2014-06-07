package net.nemerosa.ontrack.model.structure;

import lombok.Data;

import java.beans.ConstructorProperties;
import java.util.Collections;
import java.util.List;

/**
 * Request data for the creation of a build.
 * <p/>
 * Note that the version <i>without</i> the properties is usually the one which
 * is proposed to human clients when the one with properties is the one proposed
 * to automation clients (like CI engines).
 */
@Data
public class BuildRequest {

    private final String name;
    private final String description;
    private final List<PropertyCreationRequest> properties;

    @ConstructorProperties({"name", "description", "properties"})
    public BuildRequest(String name, String description, List<PropertyCreationRequest> properties) {
        this.name = name;
        this.description = description;
        this.properties = properties != null ? properties : Collections.emptyList();
    }

    public NameDescription asNameDescription() {
        return new NameDescription(name, description);
    }
}
