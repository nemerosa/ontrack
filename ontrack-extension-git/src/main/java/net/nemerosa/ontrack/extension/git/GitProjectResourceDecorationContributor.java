package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.structure.Project;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.ui.resource.LinksBuilder;
import net.nemerosa.ontrack.ui.resource.ResourceDecorationContributor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class GitProjectResourceDecorationContributor implements ResourceDecorationContributor {

    private final GitService gitService;

    @Autowired
    public GitProjectResourceDecorationContributor(GitService gitService) {
        this.gitService = gitService;
    }

    @Override
    public void contribute(LinksBuilder linksBuilder, ProjectEntity projectEntity) {
        if (projectEntity.getProjectEntityType() == ProjectEntityType.PROJECT) {
            Project project = (Project) projectEntity;
            if (gitService.getProjectConfiguration(project).isPresent()) {
                linksBuilder.link(
                        "_gitSync",
                        on(GitController.class).getProjectGitSyncInfo(project.getId()),
                        ProjectConfig.class, projectEntity
                );
            }
        }
    }
}
