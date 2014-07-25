package net.nemerosa.ontrack.extension.git.resource;

import net.nemerosa.ontrack.extension.git.GitController;
import net.nemerosa.ontrack.extension.git.model.GitChangeLog;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class GitChangeLogResourceDecorator extends AbstractResourceDecorator<GitChangeLog> {

    public GitChangeLogResourceDecorator() {
        super(GitChangeLog.class);
    }

    @Override
    public List<Link> links(GitChangeLog changeLog, ResourceContext resourceContext) {
        return resourceContext.links()
                .link("_commits", on(GitController.class).changeLogCommits(changeLog.getUuid()))
                        // TODO .link("_issues", on(SVNController.class).changeLogIssues(changeLog.getUuid()), changeLog.getScmBranch().getConfiguredIssueService() != null)
                        // TODO .link("_files", on(SVNController.class).changeLogFiles(changeLog.getUuid()))
                .build();
    }

}
