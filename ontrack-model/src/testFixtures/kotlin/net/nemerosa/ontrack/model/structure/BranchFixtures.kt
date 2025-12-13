package net.nemerosa.ontrack.model.structure

object BranchFixtures {

    fun testBranch(
        id: Int = 10,
        project: Project = ProjectFixtures.testProject(),
        name: String = "main",
        disabled: Boolean = false,
    ) = Branch(
        id = ID.of(id),
        name = name,
        description = "main branch",
        isDisabled = disabled,
        project = project,
        signature = Signature.of("test"),
    )

}