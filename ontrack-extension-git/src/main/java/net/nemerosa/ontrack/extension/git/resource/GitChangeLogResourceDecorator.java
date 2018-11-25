package net.nemerosa.ontrack.extension.git.resource;

import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest;
import net.nemerosa.ontrack.extension.git.GitController;
import net.nemerosa.ontrack.extension.git.model.GitChangeLog;
import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.extension.scm.SCMController;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

@Component
public class GitChangeLogResourceDecorator extends AbstractResourceDecorator<GitChangeLog> {

    private final GitService gitService;

    @Autowired
    public GitChangeLogResourceDecorator(GitService gitService) {
        super(GitChangeLog.class);
        this.gitService = gitService;
    }

    @Override
    public List<Link> links(GitChangeLog changeLog, ResourceContext resourceContext) {
        // Issues
        boolean issues = false;
        GitConfiguration configuration = gitService.getProjectConfiguration(changeLog.getProject());
        if (configuration != null) {
            issues = configuration.getConfiguredIssueService().isPresent();
        }
        // Links
        return resourceContext.links()
                .link("_commits", on(GitController.class).changeLogCommits(changeLog.getUuid()))
                .link(
                        "_issues",
                        on(GitController.class).changeLogIssues(changeLog.getUuid()),
                        issues
                )
                .link("_files", on(GitController.class).changeLogFiles(changeLog.getUuid()))
                .link("_changeLogFileFilters", on(SCMController.class).getChangeLogFileFilters(changeLog.getProject().getId()))
                .link("_diff", on(GitController.class).diff(null))
                .link(
                        "_exportFormats",
                        on(GitController.class).changeLogExportFormats(changeLog.getProject().getId())
                )
                .link("_exportIssues", on(GitController.class).changeLog(new IssueChangeLogExportRequest()))
                .page(
                        "_page",
                        "extension/git/changelog?from=%d&to=%d",
                        changeLog.getFrom().getBuild().id(),
                        changeLog.getTo().getBuild().id()
                )
                .build();
    }

}
