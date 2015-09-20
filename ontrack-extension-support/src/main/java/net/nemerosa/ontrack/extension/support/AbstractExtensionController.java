package net.nemerosa.ontrack.extension.support;

import net.nemerosa.ontrack.model.extension.ExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;
import net.nemerosa.ontrack.ui.resource.Resource;

// TODO Interceptor to check the feature activation
public abstract class AbstractExtensionController<F extends ExtensionFeature> extends AbstractResourceController {

    protected final F feature;

    public AbstractExtensionController(F feature) {
        this.feature = feature;
    }

    public abstract Resource<ExtensionFeatureDescription> getDescription();

}
