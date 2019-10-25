package net.nemerosa.ontrack.extension.issues.support

import net.nemerosa.ontrack.extension.api.model.IssueChangeLogExportRequest
import net.nemerosa.ontrack.extension.issues.export.IssueExportService
import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueExportMoreThanOneGroupException
import net.nemerosa.ontrack.extension.issues.model.IssueServiceConfiguration
import java.util.*
import java.util.function.BiFunction

/**
 * Utility class to deal with issues.
 */
object IssueServiceUtils {

    /**
     * Collects the group(s) an issue belongs to according to its own list of types
     * and a grouping specification.
     *
     * @param issueTypes            Issue types
     * @param groupingSpecification Group -&gt; (Group types)
     * @return List of group the issue belongs to
     */
    @JvmStatic
    fun getIssueGroups(issueTypes: Collection<String>, groupingSpecification: Map<String, Set<String>>): Set<String> {
        val groups = HashSet<String>()
        for (issueType in issueTypes) {
            for ((groupName, groupTypes) in groupingSpecification) {
                if (groupTypes.contains(issueType)) {
                    groups.add(groupName)
                }
            }
        }
        return groups
    }

    @JvmStatic
    fun groupIssues(
            issueServiceConfiguration: IssueServiceConfiguration,
            issues: List<Issue>,
            request: IssueChangeLogExportRequest,
            issueTypesFn: BiFunction<IssueServiceConfiguration, Issue, Set<String>>
    ): Map<String, List<Issue>> {
        // Excluded issues
        val excludedTypes = request.excludedTypes
        // Gets the grouping specification
        val groupingSpecification = request.groupingSpecification
        // Map of issues, ordered by group
        val groupedIssues = mutableMapOf<String, MutableList<Issue>>()
        // Pre-enter the empty group list, in order to guarantee the ordering
        for (groupName in groupingSpecification.keys) {
            groupedIssues[groupName] = ArrayList()
        }
        // For all issues
        for (issue in issues) {
            // Issue type(s)
            val issueTypes = issueTypesFn.apply(issueServiceConfiguration, issue)
            // Excluded issue?
            if (Collections.disjoint(excludedTypes, issueTypes)) {
                // Issue is not excluded
                // Gets the groups this issue belongs to
                val issueGroups = getIssueGroups(issueTypes, groupingSpecification)
                // Target group
                val targetGroup: String = if (issueGroups.size > 1) {
                    throw IssueExportMoreThanOneGroupException(issue.key, issueGroups)
                } else if (issueGroups.isEmpty()) {
                    if (groupingSpecification.isEmpty()) {
                        IssueExportService.NO_GROUP
                    } else {
                        request.altGroup
                    }
                } else {
                    issueGroups.first()
                }
                // Grouping
                val issueList = groupedIssues.computeIfAbsent(targetGroup) { mutableListOf() }
                issueList.add(issue)
            }
        }
        // Prunes empty groups
        groupedIssues.entries.removeIf { entry -> entry.value.isEmpty() }
        // OK
        return groupedIssues
    }
}
