package net.nemerosa.ontrack.extension.issues.support

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

}
