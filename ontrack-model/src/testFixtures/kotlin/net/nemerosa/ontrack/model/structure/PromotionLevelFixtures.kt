package net.nemerosa.ontrack.model.structure

object PromotionLevelFixtures {

    fun testPromotionLevel(
        branch: Branch = BranchFixtures.testBranch(),
        name: String = "PL",
    ) = PromotionLevel(
        id = ID.of(100),
        name = name,
        description = null,
        signature = Signature.of("test"),
        branch = branch,
        isImage = false,
    )

}