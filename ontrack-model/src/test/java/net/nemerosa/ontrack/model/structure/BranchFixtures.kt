package net.nemerosa.ontrack.model.structure

object BranchFixtures {

    fun testBranch(
        project: Project = ProjectFixtures.testProject()
    ) = Branch(
        id = ID.of(10),
        name = "main",
        description = "main branch",
        isDisabled = false,
        project = project,
        signature = Signature.of("test"),
    )

}