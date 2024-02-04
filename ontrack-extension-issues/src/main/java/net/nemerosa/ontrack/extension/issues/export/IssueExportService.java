package net.nemerosa.ontrack.extension.issues.export;

import net.nemerosa.ontrack.extension.issues.IssueServiceExtension;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;

import java.util.List;
import java.util.Map;

/**
 * @deprecated Use the templating service.
 */
@Deprecated
public interface IssueExportService {

    String NO_GROUP = "";

    ExportFormat getExportFormat();

    ExportedIssues export(
            IssueServiceExtension issueServiceExtension,
            IssueServiceConfiguration issueServiceConfiguration,
            Map<String, List<Issue>> groupedIssues);
}