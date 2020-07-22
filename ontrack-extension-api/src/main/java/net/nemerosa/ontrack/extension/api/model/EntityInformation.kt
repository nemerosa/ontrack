package net.nemerosa.ontrack.extension.api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import net.nemerosa.ontrack.extension.api.EntityInformationExtension;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription;

/**
 * Information returned by an {@link net.nemerosa.ontrack.extension.api.EntityInformationExtension}.
 */
@Data
public class EntityInformation {

    /**
     * Object which has returned the information
     */
    @JsonIgnore
    private final EntityInformationExtension extension;

    /**
     * Data
     */
    private final Object data;

    /**
     * Information type
     */
    public String getType() {
        return extension.getClass().getName();
    }

    /**
     * Extension feature
     */
    public ExtensionFeatureDescription getFeature() {
        return extension.getFeature().getFeatureDescription();
    }

}
