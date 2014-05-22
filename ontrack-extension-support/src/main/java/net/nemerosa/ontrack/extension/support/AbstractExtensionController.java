package net.nemerosa.ontrack.extension.support;

import net.nemerosa.ontrack.extension.api.ExtensionFeature;
import net.nemerosa.ontrack.extension.api.ExtensionFeatureDescription;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resource;

public abstract class AbstractExtensionController<F extends ExtensionFeature> extends AbstractResourceController {

    protected final F feature;

    public AbstractExtensionController(F feature) {
        this.feature = feature;
    }

    public abstract Resource<ExtensionFeatureDescription> getDescription();

}
