package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.svn.service.SVNService;
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
public class SVNBuildResourceDecorationContributor implements ResourceDecorationContributor {

    private final SVNService svnService;

    @Autowired
    public SVNBuildResourceDecorationContributor(SVNService svnService) {
        this.svnService = svnService;
    }

    @Override
    public void contribute(LinksBuilder linksBuilder, ProjectEntity projectEntity) {
        if (projectEntity.getProjectEntityType() == ProjectEntityType.BUILD) {
            Build build = (Build) projectEntity;
            if (svnService.getSVNRepository(build.getBranch()).isPresent()) {
                BuildDiffRequest request = new BuildDiffRequest();
                request.setFrom(build.getId());
                linksBuilder.link(
                        "_changeLog",
                        MvcUriComponentsBuilder.on(SVNController.class).changeLog(request),
                        ProjectView.class,
                        build
                );
                linksBuilder.page(
                        "_changeLogPage",
                        "extension/svn/changelog",
                        ProjectView.class,
                        build
                );
            }
        }
    }
}
