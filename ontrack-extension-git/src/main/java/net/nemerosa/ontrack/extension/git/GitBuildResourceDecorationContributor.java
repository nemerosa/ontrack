package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.model.security.ProjectView;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.ui.resource.LinkDefinition;
import net.nemerosa.ontrack.ui.resource.LinkDefinitions;
import net.nemerosa.ontrack.ui.resource.ResourceDecorationContributor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Component
public class GitBuildResourceDecorationContributor implements ResourceDecorationContributor<Build> {

    private final GitService gitService;

    @Autowired
    public GitBuildResourceDecorationContributor(GitService gitService) {
        this.gitService = gitService;
    }

    @Override
    public List<LinkDefinition<Build>> getLinkDefinitions() {
        return Arrays.asList(
                LinkDefinitions.link(
                        "_changeLog",
                        build -> MvcUriComponentsBuilder.on(GitController.class).changeLog(new BuildDiffRequest().withFrom(build.getId())),
                        (build, resourceContext) -> resourceContext.isProjectFunctionGranted(build, ProjectView.class) &&
                                gitService.isBranchConfiguredForGit(build.getBranch())
                ),
                LinkDefinitions.page(
                        "_changeLogPage",
                        (build, resourceContext) -> resourceContext.isProjectFunctionGranted(build, ProjectView.class) &&
                                gitService.isBranchConfiguredForGit(build.getBranch())
                        ,
                        "extension/git/changelog"
                )
        );
    }

    @Override
    public boolean applyTo(Class<? extends ProjectEntity> projectClass) {
        return Build.class.isAssignableFrom(projectClass);
    }
}
