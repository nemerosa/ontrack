package net.nemerosa.ontrack.extension.general;

import lombok.Data;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.BuildSearchForm;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.StructureService;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Data
@Deprecated
public class BuildLinkPropertyItem {

    private final String project;
    private final String build;

    public static BuildLinkPropertyItem of(String project, String build) {
        return new BuildLinkPropertyItem(project, build);
    }

    public static BuildLinkPropertyItem of(Build build) {
        return of(build.getBranch().getProject().getName(), build.getName());
    }

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

    /**
     * Does one of the items match the project-&gt;build? The value can be blank (meaning all values)
     * or contains wildcards (*).
     */
    public boolean match(String projectPattern, String buildPattern) {
        return StringUtils.equals(this.project, projectPattern) &&
                (
                        StringUtils.isBlank(buildPattern) ||
                                StringUtils.equals("*", buildPattern) ||
                                Pattern.matches(StringUtils.replace(buildPattern, "*", ".*"), this.build)
                );
    }

}
