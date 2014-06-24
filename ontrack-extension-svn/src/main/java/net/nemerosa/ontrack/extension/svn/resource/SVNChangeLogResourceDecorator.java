package net.nemerosa.ontrack.extension.svn.resource;

import net.nemerosa.ontrack.extension.svn.SVNController;
import net.nemerosa.ontrack.extension.svn.model.SVNChangeLog;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class SVNChangeLogResourceDecorator extends AbstractResourceDecorator<SVNChangeLog> {

    public SVNChangeLogResourceDecorator() {
        super(SVNChangeLog.class);
    }

    @Override
    public List<Link> links(SVNChangeLog changeLog, ResourceContext resourceContext) {
        return resourceContext.links()
                .link("_revisions", on(SVNController.class).changeLogRevisions(changeLog.getUuid()))
                .link("_issues", on(SVNController.class).changeLogIssues(changeLog.getUuid()), changeLog.getScmBranch().getConfiguredIssueService() != null)
                // TODO Files
                .build();
    }

}
