package net.nemerosa.ontrack.extension.support;

import net.nemerosa.ontrack.model.extension.ExtensionFeature;
import net.nemerosa.ontrack.model.extension.ExtensionFeatureDescription;
import net.nemerosa.ontrack.ui.controller.AbstractResourceController;

public abstract class AbstractExtensionController<F extends ExtensionFeature> extends AbstractResourceController {

    protected final F feature;

    public AbstractExtensionController(F feature) {
        this.feature = feature;
    }

    public abstract ExtensionFeatureDescription getDescription();

}
