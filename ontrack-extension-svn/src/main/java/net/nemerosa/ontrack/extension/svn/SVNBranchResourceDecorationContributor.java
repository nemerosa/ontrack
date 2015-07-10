package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.svn.service.SVNService;
import net.nemerosa.ontrack.model.structure.Branch;
import net.nemerosa.ontrack.model.structure.ProjectEntity;
import net.nemerosa.ontrack.model.structure.ProjectEntityType;
import net.nemerosa.ontrack.ui.resource.LinksBuilder;
import net.nemerosa.ontrack.ui.resource.ResourceDecorationContributor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

@Component
public class SVNBranchResourceDecorationContributor implements ResourceDecorationContributor {

    private final SVNService svnService;

    @Autowired
    public SVNBranchResourceDecorationContributor(SVNService svnService) {
        this.svnService = svnService;
    }

    @Override
    public void contribute(LinksBuilder linksBuilder, ProjectEntity projectEntity) {
        if (projectEntity.getProjectEntityType() == ProjectEntityType.BRANCH) {
            Branch branch = (Branch) projectEntity;
            if (svnService.getSVNRepository(branch).isPresent()) {
                linksBuilder.link(
                        "_download",
                        MvcUriComponentsBuilder.on(SVNController.class).download(
                                branch.getId(), ""
                        )
                );
            }
        }
    }
}
