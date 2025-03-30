package net.nemerosa.ontrack.model.structure

object ProjectFixtures {

    fun testProject(
        name: String = "project",
        disabled: Boolean = false,
    ) = Project(
        id = ID.of(1),
        name = name,
        description = "Project description",
        isDisabled = disabled,
        signature = Signature.of("test"),
    )

}