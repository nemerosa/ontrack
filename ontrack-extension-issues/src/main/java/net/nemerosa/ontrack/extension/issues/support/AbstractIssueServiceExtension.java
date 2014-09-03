package net.nemerosa.ontrack.extension.issues.support;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import net.nemerosa.ontrack.extension.api.ExtensionFeature;
import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest;
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension;
import net.nemerosa.ontrack.extension.issues.export.ExportFormat;
import net.nemerosa.ontrack.extension.issues.export.ExportedIssues;
import net.nemerosa.ontrack.extension.issues.export.IssueExportService;
import net.nemerosa.ontrack.extension.issues.export.IssueExportServiceFactory;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueExportMoreThanOneGroupException;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

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
    public List<ExportFormat> exportFormats() {
        return issueExportServiceFactory.getIssueExportServices().stream()
                .map(IssueExportService::getExportFormat)
                .collect(Collectors.toList());
    }

    @Override
    public ExportedIssues exportIssues(IssueServiceConfiguration issueServiceConfiguration, List<? extends Issue> issues, IssueChangeLogExportRequest request) {
        // Grouping of issues (or not)
        Map<String, List<Issue>> groupedIssues = groupIssues(issueServiceConfiguration, issues, request);
        // Export service
        IssueExportService exportService = issueExportServiceFactory.getIssueExportService(request.getFormat());
        // Exporting
        return exportService.export(
                this,
                issueServiceConfiguration,
                groupedIssues
        );
    }

    protected Map<String, List<Issue>> groupIssues(IssueServiceConfiguration issueServiceConfiguration, List<? extends Issue> issues, IssueChangeLogExportRequest request) {
        Map<String, List<Issue>> groupedIssues = new LinkedHashMap<>();
        // Excluded issues
        Set<String> excludedTypes = new HashSet<>();
        String exclude = request.getExclude();
        if (StringUtils.isNotBlank(exclude)) {
            excludedTypes = Sets.newHashSet(StringUtils.split(exclude));
        }
        // Gets the grouping specification
        Map<String, Set<String>> groupingSpecification = request.getGroupingSpecification();
        // For all issues
        for (Issue issue : issues) {
            // Issue type(s)
            Set<String> issueTypes = getIssueTypes(issueServiceConfiguration, issue);
            // Excluded issue?
            if (Collections.disjoint(excludedTypes, issueTypes)) {
                // Issue is not excluded
                // Gets the groups this issue belongs to
                Set<String> issueGroups = getIssueGroups(issueTypes, groupingSpecification);
                // Target group
                String targetGroup;
                if (issueGroups.size() > 1) {
                    throw new IssueExportMoreThanOneGroupException(issue.getKey(), issueGroups);
                } else if (issueGroups.isEmpty()) {
                    if (groupingSpecification.isEmpty()) {
                        targetGroup = IssueExportService.NO_GROUP;
                    } else {
                        targetGroup = request.getAltGroup();
                    }
                } else {
                    targetGroup = Iterables.get(issueGroups, 0);
                }
                // Grouping
                List<Issue> issueList = groupedIssues.get(targetGroup);
                if (issueList == null) {
                    issueList = new ArrayList<>();
                    groupedIssues.put(targetGroup, issueList);
                }
                issueList.add(issue);
            }
        }
        // OK
        return groupedIssues;
    }

    protected abstract Set<String> getIssueTypes(IssueServiceConfiguration issueServiceConfiguration, Issue issue);

    protected Set<String> getIssueGroups(Set<String> issueTypes, Map<String, Set<String>> groupingSpecification) {
        Set<String> groups = new HashSet<>();
        for (String issueType : issueTypes) {
            for (Map.Entry<String, Set<String>> entry : groupingSpecification.entrySet()) {
                String groupName = entry.getKey();
                Set<String> groupTypes = entry.getValue();
                if (groupTypes.contains(issueType)) {
                    groups.add(groupName);
                }
            }
        }
        return groups;
    }

}
