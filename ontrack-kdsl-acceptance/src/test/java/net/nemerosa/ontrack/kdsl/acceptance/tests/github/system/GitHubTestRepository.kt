package net.nemerosa.ontrack.kdsl.acceptance.tests.github.system

import net.nemerosa.ontrack.kdsl.spec.Branch

fun withTestGitHubRepository(
    code: GitHubRepositoryContext.() -> Unit,
) {
    TODO()
}

class GitHubRepositoryContext {

    fun repositoryFile(
        path: String,
        branch: String = "main",
        content: () -> String,
    ) {
        TODO()
    }

    fun Branch.configuredForGitHubRepository(
        scmBranch: String = "main",
    ) {
        TODO()
    }

    fun assertThatGitHubRepository(
        code: AssertionContext.() -> Unit,
    ) {
        val context = AssertionContext()
        context.code()
    }

    inner class AssertionContext {
        fun hasPR(from: String, to: String) {
            TODO("Not yet implemented")
        }

        fun fileContains(
            path: String,
            branch: String = "main",
            content: () -> String,
        ) {
            TODO("Not yet implemented")
        }

    }

}
