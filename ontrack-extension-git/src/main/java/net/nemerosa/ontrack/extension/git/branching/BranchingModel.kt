package net.nemerosa.ontrack.extension.git.branching

import net.nemerosa.ontrack.model.structure.Branch

/**
 * Branching model for a project.
 *
 * @property patterns Maps the _type_ of branch to a
 *                    regular expressions.
 */
class BranchingModel(
        val patterns: Map<String, String>
) {
    companion object {
        /**
         * Default branching model to use
         * when no model if defined globally or at
         * project level.
         *
         * It defines a unique type, "Releases", mapped
         * to the `release/.*` regular expression.
         */
        val DEFAULT = BranchingModel(
                mapOf(
                        "Releases" to "release/.*"
                )
        )
    }

    // TODO Parsing the model from a text
    // TODO Rendering the model into a text
    // TODO Checks the validity of the model (no repetition)

    /**
     * Given this branching model, a list of branches
     * and a way to get the actual Git branch, returns
     * an indexed list of branches, sorted from the
     * oldest to the newest.
     *
     * @param branches List of branches to index
     * @param gitBranchAccessor Function to get the Git branch
     * @return Associates each type with a list of matching
     * branches, sorted from the oldest to the newest.
     */
    fun groupBranches(
            branches: List<Branch>,
            gitBranchAccessor: (Branch) -> String?
    ): Map<String, List<Branch>> =
            patterns.mapValues { (_, regex) ->
                branches.filter { branch ->
                    val gitBranch = gitBranchAccessor(branch)
                    gitBranch != null && regex.toRegex().matches(gitBranch)
                }.sortedBy { it.id() }
            }
}