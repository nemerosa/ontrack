package net.nemerosa.ontrack.boot.resources;

import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.PromotionLevel;
import net.nemerosa.ontrack.ui.resource.AbstractResourceModule;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

public class CoreResourceModule extends AbstractResourceModule {

    public CoreResourceModule(ResourceContext resourceContext) {
        super("ontrack", resourceContext);
    }

    @Override
    protected void setupResources() {
        register(
                Project.class,
                ProjectResource::new
        );
        register(
                Branch.class,
                BranchResource::new
        );
        register(
                PromotionLevel.class,
                PromotionLevelResource::new
        );
    }
}
