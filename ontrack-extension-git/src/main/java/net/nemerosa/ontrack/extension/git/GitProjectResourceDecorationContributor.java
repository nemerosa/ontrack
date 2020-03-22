package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.ui.resource.LinkDefinition;
import net.nemerosa.ontrack.ui.resource.LinkDefinitions;
import net.nemerosa.ontrack.ui.resource.ResourceDecorationContributor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class GitProjectResourceDecorationContributor implements ResourceDecorationContributor<Project> {

    private final GitService gitService;

    @Autowired
    public GitProjectResourceDecorationContributor(GitService gitService) {
        this.gitService = gitService;
    }

    @Override
    public List<LinkDefinition<Project>> getLinkDefinitions() {
        return Collections.singletonList(
                LinkDefinitions.link(
                        "_gitSync",
                        project -> on(GitController.class).getProjectGitSyncInfo(project.getId()),
                        (project, rc) -> rc.isProjectFunctionGranted(project, ProjectConfig.class) &&
                                gitService.isProjectConfiguredForGit(project)
                )
        );
    }

    @Override
    public boolean applyTo(ProjectEntityType projectEntityType) {
        return projectEntityType == ProjectEntityType.PROJECT;
    }
}
