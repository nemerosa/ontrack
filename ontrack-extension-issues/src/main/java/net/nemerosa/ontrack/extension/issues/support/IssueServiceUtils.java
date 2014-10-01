package net.nemerosa.ontrack.extension.issues.support;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
     * @param groupingSpecification Group -> (Group types)
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
}
