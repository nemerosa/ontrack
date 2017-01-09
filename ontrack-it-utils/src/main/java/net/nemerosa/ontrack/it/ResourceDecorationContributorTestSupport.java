package net.nemerosa.ontrack.it;

import net.nemerosa.ontrack.model.security.SecurityService;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.ui.controller.MockURIBuilder;
import net.nemerosa.ontrack.ui.resource.*;

public final class ResourceDecorationContributorTestSupport {

    /**
     * The BuildResourceDecorator is NOT loaded since it belongs to the "ui" module.
     * <p>
     * See if core resource decorators could be put in a separate module. They cannot because
     * they themselves rely on the UI controllers.
     * <p>
     * So, in order to test a resource decoration contributor in an extension, we'd have to load
     * the complete UI module, which is not very practical.
     * <p>
     * We can, on the other hand, create a fake resource decorator to wrap the resource decoration contributor
     * to test.
     */
    public static <T extends ProjectEntity> ResourceObjectMapper createResourceObjectMapper(
            Class<T> type,
            ResourceDecorationContributor<T> contributor,
            SecurityService securityService) {
        return new ResourceObjectMapperFactory().resourceObjectMapper(
                new DefaultResourceContext(new MockURIBuilder(), securityService),
                ResourceDecorators.decoratorWithExtension(type, contributor)
        );
    }

}
