package net.nemerosa.ontrack.extension.issues.support;

import com.google.common.collect.Iterables;
import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest;
import net.nemerosa.ontrack.extension.issues.export.IssueExportService;
import net.nemerosa.ontrack.extension.issues.model.Issue;
import net.nemerosa.ontrack.extension.issues.model.IssueExportMoreThanOneGroupException;
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration;

import java.util.*;
import java.util.function.BiFunction;

/**
 * Utility class to deal with issues.
 */
public final class IssueServiceUtils {

    /**
     * Only static methods
     */
    private IssueServiceUtils() {
    }

    /**
     * Collects the group(s) an issue belongs to according to its own list of types
     * and a grouping specification.
     *
     * @param issueTypes            Issue types
     * @param groupingSpecification Group -&gt; (Group types)
     * @return List of group the issue belongs to
     */
    public static Set<String> getIssueGroups(Collection<String> issueTypes, Map<String, Set<String>> groupingSpecification) {
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

    public static Map<String, List<Issue>> groupIssues(
            IssueServiceConfiguration issueServiceConfiguration,
            List<? extends Issue> issues,
            IssueChangeLogExportRequest request,
            BiFunction<IssueServiceConfiguration, Issue, Set<String>> issueTypesFn
    ) {
        // Excluded issues
        Set<String> excludedTypes = request.getExcludedTypes();
        // Gets the grouping specification
        Map<String, Set<String>> groupingSpecification = request.getGroupingSpecification();
        // Map of issues, ordered by group
        Map<String, List<Issue>> groupedIssues = new LinkedHashMap<>();
        // Pre-enter the empty group list, in order to guarantee the ordering
        for (String groupName : groupingSpecification.keySet()) {
            groupedIssues.put(groupName, new ArrayList<>());
        }
        // For all issues
        for (Issue issue : issues) {
            // Issue type(s)
            Set<String> issueTypes = issueTypesFn.apply(issueServiceConfiguration, issue);
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
        // Prunes empty groups
        Iterator<Map.Entry<String, List<Issue>>> iterator = groupedIssues.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<Issue>> entry = iterator.next();
            if (entry.getValue().isEmpty()) {
                iterator.remove();
            }
        }
        // OK
        return groupedIssues;
    }
}
