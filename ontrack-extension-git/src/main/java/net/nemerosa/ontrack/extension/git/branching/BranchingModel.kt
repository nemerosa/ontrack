package net.nemerosa.ontrack.extension.git.branching

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
         */
        val DEFAULT = BranchingModel(
                mapOf(
                        "Development" to "master|gatekeeper|develop",
                        "Releases" to "release/.*"
                )
        )
    }

    // TODO Parsing the model from a text
    // TODO Rendering the model into a text
    // TODO Checks the validity of the model (no repetition)

    /**
     * Given this branching model, a list of Git branches, returns
     * an indexed list of Git branches.
     *
     * @param branches List of Git branches to index
     * @return Associates each type with a list of matching
     * branches
     */
    fun groupBranches(
            branches: List<String>
    ): Map<String, List<String>> =
            patterns.mapValues { (_, regex) ->
                val pattern = regex.toRegex()
                branches.filter { pattern.matches(it) }
            }
}