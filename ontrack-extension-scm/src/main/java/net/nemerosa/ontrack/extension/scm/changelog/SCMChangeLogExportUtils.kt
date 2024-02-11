package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.extension.issues.model.Issue
import java.util.*

/**
 * Collects the group(s) an issue belongs to according to its own list of types
 * and a grouping specification.
 *
 * @param issueTypes            Issue types
 * @param groupingSpecification Group -&gt; (Group types)
 * @return List of group the issue belongs to
 */
fun getIssueGroups(issueTypes: Collection<String>, groupingSpecification: Map<String, Set<String>>): Set<String> {
    val groups = mutableSetOf<String>()
    for (issueType in issueTypes) {
        for ((groupName, groupTypes) in groupingSpecification) {
            if (groupTypes.contains(issueType)) {
                groups.add(groupName)
            }
        }
    }
    return groups
}

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
    // Default group name
    val defaultGroupName = "Other"
    // For all issues
    for (issue in issues) {
        // Issue type(s)
        val issueTypes = issueTypesFn(issue)
        // Excluded issue?
        if (Collections.disjoint(excludedTypes, issueTypes)) {
            // Issue is not excluded
            // Gets the groups this issue belongs to
            val issueGroups = getIssueGroups(issueTypes, groupingSpecification)
            // Target group
            val targetGroup: String = if (issueGroups.size > 1) {
                throw SCMChangeLogExportMoreThanOneGroupException(issue.key, issueGroups)
            } else if (issueGroups.isEmpty()) {
                if (groupingSpecification.isEmpty()) {
                    "" // No group at all
                } else {
                    input.altGroup?.takeIf { it.isNotBlank() } ?: defaultGroupName
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

class SCMChangeLogExportMoreThanOneGroupException(key: String, groups: Collection<String>) :
    BaseException("Issue $key has been assigned to more than one group: $groups", key, groups)
