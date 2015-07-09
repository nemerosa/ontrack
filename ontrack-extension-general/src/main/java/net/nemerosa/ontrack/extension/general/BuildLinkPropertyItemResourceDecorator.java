package net.nemerosa.ontrack.extension.general;

import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.BuildSearchForm;
import net.nemerosa.ontrack.model.structure.Project;
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
        // Gets the project
        Optional<Project> oProject = structureService.findProjectByName(item.getProject());
        if (oProject.isPresent()) {
            // Gets the build in the project
            List<Build> builds = structureService.buildSearch(
                    oProject.get().getId(),
                    new BuildSearchForm().withBuildName(item.getBuild()).withMaximumCount(1)
            );
            if (builds.isEmpty()) {
                return Collections.emptyList();
            } else {
                Build build = builds.get(0);
                return resourceContext.links()
                        .entityURI("_build", build, true)
                        .entityPage("_buildPage", true, build)
                        .build();
            }
        } else {
            return Collections.emptyList();
        }
    }
}
