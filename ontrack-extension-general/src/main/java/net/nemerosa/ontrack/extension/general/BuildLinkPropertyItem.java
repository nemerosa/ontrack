package net.nemerosa.ontrack.extension.general;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.BuildSearchForm;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureService;

import java.util.List;
import java.util.Optional;

@Data
public class BuildLinkPropertyItem {

    private final String project;
    private final String build;

    public Optional<Build> findBuild(StructureService structureService) {
        // Gets the project
        Optional<Project> oProject = structureService.findProjectByName(project);
        if (oProject.isPresent()) {
            // Gets the build in the project
            List<Build> builds = structureService.buildSearch(
                    oProject.get().getId(),
                    new BuildSearchForm().withBuildName(build).withMaximumCount(1)
            );
            if (builds.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(builds.get(0));
            }
        } else {
            return Optional.empty();
        }
    }

}
