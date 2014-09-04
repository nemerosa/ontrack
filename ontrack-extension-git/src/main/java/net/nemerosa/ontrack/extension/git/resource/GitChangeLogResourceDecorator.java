package net.nemerosa.ontrack.extension.git.resource;

import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest;
import net.nemerosa.ontrack.extension.git.GitController;
import net.nemerosa.ontrack.extension.git.model.GitChangeLog;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.apache.commons.lang3.StringUtils;

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
                .link("_issues", on(GitController.class).changeLogIssues(changeLog.getUuid()), StringUtils.isNotBlank(changeLog.getScmBranch().getIssueServiceConfigurationIdentifier()))
                .link("_files", on(GitController.class).changeLogFiles(changeLog.getUuid()))
                .link("_exportFormats", on(GitController.class).changeLogExportFormats(changeLog.getBranch().getId()))
                .link("_exportIssues", on(GitController.class).changeLog(new IssueChangeLogExportRequest()))
                .build();
    }

}
