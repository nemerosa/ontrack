package net.nemerosa.ontrack.model.structure

object ValidationStampFixtures {

    fun testValidationStamp(
        branch: Branch = BranchFixtures.testBranch(),
        name: String = "VS",
    ) = ValidationStamp(
        id = ID.of(100),
        name = name,
        description = null,
        signature = Signature.of("test"),
        branch = branch,
        owner = null,
        isImage = false,
        dataType = null,
    )
}