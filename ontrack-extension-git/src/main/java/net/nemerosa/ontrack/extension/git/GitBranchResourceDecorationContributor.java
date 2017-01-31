package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.model.security.ProjectConfig;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.ui.resource.LinkDefinition;
import net.nemerosa.ontrack.ui.resource.LinkDefinitions;
import net.nemerosa.ontrack.ui.resource.ResourceDecorationContributor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.Collections;
import java.util.List;

@Component
public class GitBranchResourceDecorationContributor implements ResourceDecorationContributor<Branch> {

    private final GitService gitService;

    @Autowired
    public GitBranchResourceDecorationContributor(GitService gitService) {
        this.gitService = gitService;
    }

    @Override
    public List<LinkDefinition<Branch>> getLinkDefinitions() {
        return Collections.singletonList(
                LinkDefinitions.link(
                        "_download",
                        branch -> MvcUriComponentsBuilder.on(GitController.class).download(
                                branch.getId(), null
                        ),
                        (branch, rc) -> rc.isProjectFunctionGranted(branch, ProjectConfig.class) &&
                                gitService.getBranchConfiguration(branch).isPresent()
                )
        );
    }

    @Override
    public boolean applyTo(ProjectEntityType projectEntityType) {
        return projectEntityType == ProjectEntityType.BRANCH;
    }
}
