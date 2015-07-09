package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.StructureService;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Decorator for {@link BuildLinkPropertyItem}.
 */
public class BuildLinkPropertyItemResourceDecorator extends AbstractResourceDecorator<BuildLinkPropertyItem> {

    private final StructureService structureService;

    protected BuildLinkPropertyItemResourceDecorator(StructureService structureService) {
        super(BuildLinkPropertyItem.class);
        this.structureService = structureService;
    }

    @Override
    public List<Link> links(BuildLinkPropertyItem item, ResourceContext resourceContext) {
        Optional<Build> oBuild = item.findBuild(structureService);
        if (oBuild.isPresent()) {
            Build build = oBuild.get();
            return resourceContext.links()
                    .entityURI("_build", build, true)
                    .entityPage("_buildPage", true, build)
                    .build();
        } else {
            return Collections.emptyList();
        }
    }
}
