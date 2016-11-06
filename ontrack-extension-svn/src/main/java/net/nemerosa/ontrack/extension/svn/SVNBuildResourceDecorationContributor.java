package net.nemerosa.ontrack.extension.svn;

import net.nemerosa.ontrack.extension.api.model.BuildDiffRequest;
import net.nemerosa.ontrack.extension.svn.service.SVNService;
import net.nemerosa.ontrack.model.security.ProjectView;
import net.nemerosa.ontrack.model.structure.Build;
import net.nemerosa.ontrack.ui.resource.LinkDefinition;
import net.nemerosa.ontrack.ui.resource.LinkDefinitions;
import net.nemerosa.ontrack.ui.resource.ResourceDecorationContributor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

@Component
public class SVNBuildResourceDecorationContributor implements ResourceDecorationContributor<Build> {

    private final SVNService svnService;

    @Autowired
    public SVNBuildResourceDecorationContributor(SVNService svnService) {
        this.svnService = svnService;
    }

    @Override
    public List<LinkDefinition<Build>> getLinkDefinitions() {
        return Arrays.asList(
                LinkDefinitions.link(
                        "_changeLog",
                        build -> {
                            BuildDiffRequest request = new BuildDiffRequest();
                            request.setFrom(build.getId());
                            return MvcUriComponentsBuilder.on(SVNController.class).changeLog(request);
                        },
                        (build, rc) -> rc.isProjectFunctionGranted(build, ProjectView.class) &&
                                svnService.getSVNRepository(build.getBranch()).isPresent()
                ),
                LinkDefinitions.page(
                        "_changeLogPage",
                        (build, resourceContext) -> resourceContext.isProjectFunctionGranted(build, ProjectView.class) &&
                                svnService.getSVNRepository(build.getBranch()).isPresent(),
                        "extension/svn/changelog"
                )
        );
    }

    @Override
    public boolean applyTo(Class projectClass) {
        return Build.class.isAssignableFrom(projectClass);
    }
}
