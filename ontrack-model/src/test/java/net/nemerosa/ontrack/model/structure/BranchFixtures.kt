package net.nemerosa.ontrack.model.structure

object BranchFixtures {

    fun testBranch(
        project: Project = ProjectFixtures.testProject(),
        name: String = "main",
    ) = Branch(
        id = ID.of(10),
        name = name,
        description = "main branch",
        isDisabled = false,
        project = project,
        signature = Signature.of("test"),
    )

}