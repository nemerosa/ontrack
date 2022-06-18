package net.nemerosa.ontrack.extension.scm.service

object SCMTestFixtures {

    fun createSCMPullRequest() = SCMPullRequest(
        "1",
        "PR-1",
        "https://github.com/org/repo/pulls/1",
        true,
    )

}