package net.nemerosa.ontrack.kdsl.acceptance.tests.github.support

import net.nemerosa.ontrack.kdsl.acceptance.tests.github.githubTestEnv

/**
 * DSL Client to GitHub.
 */
class GitHub {

    /**
     * Integration with a real test repository in GitHub
     */
    val repository: Repository by lazy {
        Repository(
            name = githubTestEnv.repository,
        )
    }

}