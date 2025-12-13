package net.nemerosa.ontrack.model.structure

object ValidationRunFixtures {

    fun testValidationRun(
        branch: Branch = BranchFixtures.testBranch(),
        validationStampName: String = "VS",
        validationStamp: ValidationStamp = ValidationStampFixtures.testValidationStamp(
            branch = branch,
            name = validationStampName,
        ),
        build: Build = BuildFixtures.testBuild(
            branch = branch,
        ),
        runOrder: Int = 1,
        statusID: ValidationRunStatusID = ValidationRunStatusID.STATUS_PASSED,
        description: String? = null,
    ) = ValidationRun(
        id = ID.of(100),
        build = build,
        validationStamp = validationStamp,
        runOrder = runOrder,
        data = null,
        validationRunStatuses = listOf(
            ValidationRunStatus(
                id = ID.of(101),
                signature = Signature.of("test"),
                statusID = statusID,
                description = description,
            )
        )
    )

}