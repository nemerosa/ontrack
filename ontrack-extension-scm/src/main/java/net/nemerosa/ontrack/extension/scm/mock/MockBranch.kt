package net.nemerosa.ontrack.extension.scm.mock

data class MockBranch(
    val name: String,
    val commits: MutableList<MockCommit> = mutableListOf(),
)
