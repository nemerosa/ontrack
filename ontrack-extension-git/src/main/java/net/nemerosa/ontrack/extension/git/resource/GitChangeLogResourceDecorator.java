package net.nemerosa.ontrack.extension.git.resource;

import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest;
import net.nemerosa.ontrack.extension.git.GitController;
import net.nemerosa.ontrack.extension.git.model.GitChangeLog;
import net.nemerosa.ontrack.extension.git.model.GitConfiguration;
import net.nemerosa.ontrack.extension.git.service.GitService;
import net.nemerosa.ontrack.ui.resource.AbstractResourceDecorator;
import net.nemerosa.ontrack.ui.resource.Link;
import net.nemerosa.ontrack.ui.resource.ResourceContext;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;

public class GitChangeLogResourceDecorator extends AbstractResourceDecorator<GitChangeLog> {

    private final GitService gitService;

    public GitChangeLogResourceDecorator(GitService gitService) {
        super(GitChangeLog.class);
        this.gitService = gitService;
    }

    @Override
    public List<Link> links(GitChangeLog changeLog, ResourceContext resourceContext) {
        // Issues
        boolean issues = false;
        Optional<GitConfiguration> configuration = gitService.getProjectConfiguration(changeLog.getProject());
        if (configuration.isPresent()) {
            issues = StringUtils.isNotBlank(configuration.get().getIssueServiceConfigurationIdentifier());
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
                .link(
                        "_exportFormats",
                        on(GitController.class).changeLogExportFormats(changeLog.getProject().getId())
                )
                .link("_exportIssues", on(GitController.class).changeLog(new IssueChangeLogExportRequest()))
                .build();
    }

}
