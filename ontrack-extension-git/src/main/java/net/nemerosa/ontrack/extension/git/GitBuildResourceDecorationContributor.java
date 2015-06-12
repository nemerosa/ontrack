package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.model.security.ProjectView;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.ui.resource.LinksBuilder;
import net.nemerosa.ontrack.ui.resource.ResourceDecorationContributor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@Component
public class GitBuildResourceDecorationContributor implements ResourceDecorationContributor {

    private final GitService gitService;

    @Autowired
    public GitBuildResourceDecorationContributor(GitService gitService) {
        this.gitService = gitService;
    }

    @Override
    public void contribute(LinksBuilder linksBuilder, ProjectEntity projectEntity) {
        if (projectEntity.getProjectEntityType() == ProjectEntityType.BUILD) {
            Build build = (Build) projectEntity;
            if (gitService.isBranchConfiguredForGit(build.getBranch())) {
                BuildDiffRequest request = new BuildDiffRequest();
                request.setFrom(build.getId());
                linksBuilder.link(
                        "_changeLog",
                        MvcUriComponentsBuilder.on(GitController.class).changeLog(request),
                        ProjectView.class,
                        build
                );
                linksBuilder.page(
                        "_changeLogPage",
                        "extension/git/changelog",
                        ProjectView.class,
                        build
                );
            }
        }
    }
}
