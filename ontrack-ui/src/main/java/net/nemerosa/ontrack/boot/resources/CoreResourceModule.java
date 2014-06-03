package net.nemerosa.ontrack.boot.resources;

import com.fasterxml.jackson.databind.module.SimpleModule;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.PromotionLevel;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import net.nemerosa.ontrack.ui.resource.ResourceSerializerModifier;

public class CoreResourceModule extends SimpleModule {

    private final ResourceContext resourceContext;

    public CoreResourceModule(ResourceContext resourceContext) {
        super("ontrack");
        this.resourceContext = resourceContext;
    }

    @Override
    public void setupModule(SetupContext context) {
        super.setupModule(context);
        context.addBeanSerializerModifier(new ResourceSerializerModifier<>(
                resourceContext,
                Project.class,
                ProjectResource::new
        ));
        context.addBeanSerializerModifier(new ResourceSerializerModifier<>(
                resourceContext,
                Branch.class,
                BranchResource::new
        ));
        context.addBeanSerializerModifier(new ResourceSerializerModifier<>(
                resourceContext,
                PromotionLevel.class,
                PromotionLevelResource::new
        ));
    }
}
