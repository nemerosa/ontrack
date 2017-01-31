package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.svn.service.SVNService;
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
public class SVNBranchResourceDecorationContributor implements ResourceDecorationContributor<Branch> {

    private final SVNService svnService;

    @Autowired
    public SVNBranchResourceDecorationContributor(SVNService svnService) {
        this.svnService = svnService;
    }

    @Override
    public List<LinkDefinition<Branch>> getLinkDefinitions() {
        return Collections.singletonList(
                LinkDefinitions.link(
                        "_download",
                        branch -> MvcUriComponentsBuilder.on(SVNController.class).download(
                                branch.getId(), ""
                        ),
                        (branch, rc) -> rc.isProjectFunctionGranted(branch, ProjectConfig.class) &&
                                svnService.getSVNRepository(branch).isPresent()
                )
        );
    }

    @Override
    public boolean applyTo(ProjectEntityType projectEntityType) {
        return projectEntityType == ProjectEntityType.BRANCH;
    }
}
