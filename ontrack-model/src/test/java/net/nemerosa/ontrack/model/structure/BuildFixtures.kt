package net.nemerosa.ontrack.model.structure

object BuildFixtures {

    fun testBuild(
        branch: Branch = BranchFixtures.testBranch(),
        name: String = "1",
    ) = Build(
        id = ID.of(1000),
        name = name,
        description = null,
        signature = Signature.of("test"),
        branch = branch,
    )

}