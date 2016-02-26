package net.nemerosa.ontrack.model.extension;

import lombok.Data;

@Data
public class ExtensionFeatureDescription {

    private final String id;
    private final String name;
    private final String description;
    private final String version;
    private final ExtensionFeatureOptions options;

}
