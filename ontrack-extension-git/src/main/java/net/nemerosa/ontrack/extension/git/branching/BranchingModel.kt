package net.nemerosa.ontrack.extension.git.branching

import net.nemerosa.ontrack.model.support.NameValue

/**
 * Branching model for a project.
 *
 * @property patterns Maps the _type_ of branch to a
 *                    regular expressions.
 */
class BranchingModel(
        val patterns: Map<String, String>
) {
    constructor(patterns: List<NameValue>) : this(
            patterns.associate { it.name to it.value }
    )

    companion object {
        /**
         * Default branching model to use
         * when no model if defined globally or at
         * project level.
         */
        val DEFAULT = BranchingModel(
                mapOf(
                        "Development" to "master|develop",
                        "Releases" to "release/.*"
                )
        )
    }

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