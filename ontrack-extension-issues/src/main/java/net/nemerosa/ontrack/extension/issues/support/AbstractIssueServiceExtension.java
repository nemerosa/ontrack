package net.nemerosa.ontrack.extension.issues.support;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import net.nemerosa.ontrack.extension.api.ExtensionFeature;
import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest;
import net.nemerosa.ontrack.extension.issues.IssueServiceExtension;
import net.nemerosa.ontrack.extension.issues.model.*;
import net.nemerosa.ontrack.extension.support.AbstractExtension;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

import static java.lang.String.format;

/**
 * Convenient implementation for most of the issue services.
 */
public abstract class AbstractIssueServiceExtension extends AbstractExtension implements IssueServiceExtension {

    private final String id;
    private final String name;

    /**
     * Constructor.
     *
     * @param id   The unique ID for this service.
     * @param name The display name for this service.
     */
    protected AbstractIssueServiceExtension(ExtensionFeature extensionFeature, String id, String name) {
        super(extensionFeature);
        this.id = id;
        this.name = name;
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
        return Arrays.asList(
                ExportFormat.TEXT,
                ExportFormat.HTML
        );
    }

    @Override
    public ExportedIssues exportIssues(IssueServiceConfiguration issueServiceConfiguration, List<? extends Issue> issues, IssueChangeLogExportRequest request) {
        // Grouping of issues (or not)
        Map<String, List<Issue>> groupedIssues = groupIssues(issueServiceConfiguration, issues, request);
        if (ExportFormat.TEXT.getId().equals(request.getFormat())) {
            return new ExportedIssues(
                    ExportFormat.TEXT.getId(),
                    exportAsText(groupedIssues)
            );
//        } else if (ExportFormat.HTML.getId().equals(request.getFormat())) {
//            return new ExportedIssues(
//                    ExportFormat.HTML.getId(),
//                    exportAsHtml(issueServiceConfiguration, groupedIssues)
//            );
        } else {
            throw new IssueExportFormatNotFoundException(request.getFormat());
        }
    }

    protected String exportAsText(Map<String, List<Issue>> groupedIssues) {
        StringBuilder s = new StringBuilder();

        for (Map.Entry<String, List<Issue>> groupEntry : groupedIssues.entrySet()) {
            String groupName = groupEntry.getKey();
            List<Issue> issues = groupEntry.getValue();
            // Group header
            s.append(format("%s:%n%n", groupName));
            // List of issues
            for (Issue issue : issues) {
                s.append(format("* %s %s%n", issue.getKey(), issue.getSummary()));
            }
            // Group separator
            s.append(format("%n"));
        }

        return s.toString();
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
                    targetGroup = request.getAltGroup();
                } else {
                    targetGroup = Iterables.get(issueGroups, 0);
                }
                // Grouping
                if (StringUtils.isNotBlank(targetGroup)) {
                    List<Issue> issueList = groupedIssues.get(targetGroup);
                    if (issueList == null) {
                        issueList = new ArrayList<>();
                        groupedIssues.put(targetGroup, issueList);
                    }
                    issueList.add(issue);
                }
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
