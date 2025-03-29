package net.nemerosa.ontrack.boot.resources;

import jakarta.annotation.PostConstruct;
import net.nemerosa.ontrack.common.RunProfile;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Removing the version of extensions in DEV mode
 */
@Component
@Profile(RunProfile.DEV)
public class DevExtensionFeatureDescriptionResourceDecorator extends AbstractResourceDecorator<ExtensionFeatureDescription> {

    private final Logger logger = LoggerFactory.getLogger(DevExtensionFeatureDescriptionResourceDecorator.class);

    public DevExtensionFeatureDescriptionResourceDecorator() {
        super(ExtensionFeatureDescription.class);
    }

    @PostConstruct
    public void warning() {
        logger.warn("Running in DEV mode - removing versions of extensions");
    }

    @Override
    public ExtensionFeatureDescription decorateBeforeSerialization(ExtensionFeatureDescription o) {
        return new ExtensionFeatureDescription(
                o.getId(),
                o.getName(),
                o.getDescription(),
                "",
                o.getOptions()
        );
    }
}
