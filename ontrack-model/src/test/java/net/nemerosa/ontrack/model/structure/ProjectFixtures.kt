package net.nemerosa.ontrack.model.structure

object ProjectFixtures {

    fun testProject(
        name: String = "project",
    ) = Project(
        id = ID.of(1),
        name = name,
        description = "Project description",
        isDisabled = false,
        signature = Signature.of("test"),
    )

}