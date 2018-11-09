package net.nemerosa.ontrack.extension.issues.support;

import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest;
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension;
import net.nemerosa.ontrack.extension.issues.export.ExportFormat;
import net.nemerosa.ontrack.extension.issues.export.ExportedIssues;
import net.nemerosa.ontrack.extension.issues.export.IssueExportService;
import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import net.nemerosa.ontrack.model.extension.ExtensionFeature;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static net.nemerosa.ontrack.extension.issues.support.IssueServiceUtils.groupIssues;

/**
 * Convenient implementation for most of the issue services.
 */
public abstract class AbstractIssueServiceExtension extends AbstractExtension implements IssueServiceExtension {

    private final String id;
    private final String name;
    private final IssueExportServiceFactory issueExportServiceFactory;

    /**
     * Constructor.
     *
     * @param id                        The unique ID for this service.
     * @param name                      The display name for this service.
     * @param issueExportServiceFactory Factory to get export services
     */
    protected AbstractIssueServiceExtension(ExtensionFeature extensionFeature, String id, String name, IssueExportServiceFactory issueExportServiceFactory) {
        super(extensionFeature);
        this.id = id;
        this.name = name;
        this.issueExportServiceFactory = issueExportServiceFactory;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean containsIssueKey(IssueServiceConfiguration issueServiceConfiguration, String key, Set<String> keys) {
        return keys.contains(key);
    }

    /**
     * Export of both text and HTML by default.
     */
    @Override
    public List<ExportFormat> exportFormats(IssueServiceConfiguration issueServiceConfiguration) {
        return issueExportServiceFactory.getIssueExportServices().stream()
                .map(IssueExportService::getExportFormat)
                .collect(Collectors.toList());
    }

    @Override
    public ExportedIssues exportIssues(IssueServiceConfiguration issueServiceConfiguration, List<? extends Issue> issues, IssueChangeLogExportRequest request) {
        // Grouping of issues (or not)
        Map<String, List<Issue>> groupedIssues = groupIssues(
                issueServiceConfiguration,
                issues,
                request,
                this::getIssueTypes
        );
        // Export service
        IssueExportService exportService = issueExportServiceFactory.getIssueExportService(request.getFormat());
        // Exporting
        return exportService.export(
                this,
                issueServiceConfiguration,
                groupedIssues
        );
    }

    protected abstract Set<String> getIssueTypes(IssueServiceConfiguration issueServiceConfiguration, Issue issue);

}
