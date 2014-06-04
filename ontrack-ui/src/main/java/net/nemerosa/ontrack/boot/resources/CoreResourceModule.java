package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.ui.resource.AbstractResourceModule;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;

import java.util.Arrays;
import java.util.Collection;

public class CoreResourceModule extends AbstractResourceModule {

    @Override
    public Collection<ResourceDecorator<?>> decorators() {
        return Arrays.asList(
                new ProjectResourceDecorator(),
                new BranchResourceDecorator(),
                new PromotionLevelResourceDecorator(),
                new ValidationStampResourceDecorator(),
                new BuildResourceDecorator(),
                new PromotionRunResourceDecorator(),
                new ValidationRunResourceDecorator()
        );
    }
}
