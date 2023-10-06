package net.nemerosa.ontrack.model.structure

object BranchFixtures {

    fun testBranch(
        id: Int = 10,
        project: Project = ProjectFixtures.testProject(),
        name: String = "main",
    ) = Branch(
        id = ID.of(id),
        name = name,
        description = "main branch",
        isDisabled = false,
        project = project,
        signature = Signature.of("test"),
    )

}