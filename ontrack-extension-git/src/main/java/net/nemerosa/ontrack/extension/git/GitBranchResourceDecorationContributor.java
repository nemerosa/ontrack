package net.nemerosa.ontrack.extension.git;

import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.ui.resource.LinksBuilder;
import net.nemerosa.ontrack.ui.resource.ResourceDecorationContributor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@Component
public class GitBranchResourceDecorationContributor implements ResourceDecorationContributor {

    private final GitService gitService;

    @Autowired
    public GitBranchResourceDecorationContributor(GitService gitService) {
        this.gitService = gitService;
    }

    @Override
    public void contribute(LinksBuilder linksBuilder, ProjectEntity projectEntity) {
        if (projectEntity.getProjectEntityType() == ProjectEntityType.BRANCH) {
            Branch branch = (Branch) projectEntity;
            if (gitService.getBranchConfiguration(branch).isPresent()) {
                linksBuilder.link(
                        "_download",
                        MvcUriComponentsBuilder.on(GitController.class).download(
                                branch.getId(), null
                        )
                );
            }
        }
    }
}
