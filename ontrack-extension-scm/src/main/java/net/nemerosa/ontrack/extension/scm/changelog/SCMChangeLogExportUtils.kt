package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.extension.issues.model.Issue
import net.nemerosa.ontrack.extension.issues.model.IssueExportMoreThanOneGroupException
import net.nemerosa.ontrack.extension.issues.support.IssueServiceUtils
import java.util.*


fun groupIssues(
    issues: List<Issue>,
    input: SCMChangeLogExportInput,
    issueTypesFn: (issue: Issue) -> Set<String>,
): Map<String, List<Issue>> {
    // Excluded issues
    val excludedTypes = input.excludedTypes
    // Gets the grouping specification
    val groupingSpecification = input.groupingSpecification
    // Map of issues, ordered by group
    val groupedIssues = mutableMapOf<String, MutableList<Issue>>()
    // Pre-enter the empty group list, in order to guarantee the ordering
    for (groupName in groupingSpecification.keys) {
        groupedIssues[groupName] = ArrayList()
    }
    // For all issues
    for (issue in issues) {
        // Issue type(s)
        val issueTypes = issueTypesFn(issue)
        // Excluded issue?
        if (Collections.disjoint(excludedTypes, issueTypes)) {
            // Issue is not excluded
            // Gets the groups this issue belongs to
            val issueGroups = IssueServiceUtils.getIssueGroups(issueTypes, groupingSpecification)
            // Target group
            val targetGroup: String = if (issueGroups.size > 1) {
                throw IssueExportMoreThanOneGroupException(issue.key, issueGroups)
            } else if (issueGroups.isEmpty()) {
                if (groupingSpecification.isEmpty()) {
                    ""
                } else {
                    input.altGroup ?: ""
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