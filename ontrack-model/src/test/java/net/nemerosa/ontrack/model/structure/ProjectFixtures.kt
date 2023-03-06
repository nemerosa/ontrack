package net.nemerosa.ontrack.model.structure

object ProjectFixtures {

    fun testProject() = Project(
        id = ID.of(1),
        name = "project",
        description = "Project description",
        isDisabled = false,
        signature = Signature.of("test"),
    )

}