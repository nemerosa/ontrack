package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.ui.resource.AbstractResourceModule;
import net.nemerosa.ontrack.ui.resource.ResourceDecorationContributorService;
import net.nemerosa.ontrack.ui.resource.ResourceDecorator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

@Component
public class CoreResourceModule extends AbstractResourceModule {

    private final StructureService structureService;
    private final ResourceDecorationContributorService resourceDecorationContributorService;

    @Autowired
    public CoreResourceModule(StructureService structureService, ResourceDecorationContributorService resourceDecorationContributorService) {
        this.structureService = structureService;
        this.resourceDecorationContributorService = resourceDecorationContributorService;
    }

    @Override
    public Collection<ResourceDecorator<?>> decorators() {
        return Arrays.asList(
                new ConnectedAccountResourceDecorator(),
                new ProjectResourceDecorator(),
                new BranchResourceDecorator(structureService),
                new PromotionLevelResourceDecorator(),
                new ValidationStampResourceDecorator(),
                new BuildResourceDecorator(resourceDecorationContributorService),
                new PromotionRunResourceDecorator(),
                new ValidationRunResourceDecorator(),
                new BuildFilterResourceDecorator(),
                new AccountResourceDecorator(),
                new AccountGroupResourceDecorator(),
                new GlobalPermissionResourceDecorator(),
                new ProjectPermissionResourceDecorator(),
                new JobStatusResourceDecorator(),
                new PredefinedValidationStampResourceDecorator()
        );
    }
}
