package net.nemerosa.ontrack.model.structure

object PromotionRunFixtures {

    fun testPromotionRun(
        branch: Branch = BranchFixtures.testBranch(),
        promotionLevelName: String = "PL",
        promotionLevel: PromotionLevel = PromotionLevelFixtures.testPromotionLevel(
            branch = branch,
            name = promotionLevelName,
        ),
        build: Build = BuildFixtures.testBuild(
            branch = branch,
        ),
        description: String? = null,
    ) = PromotionRun(
        id = ID.of(100),
        description = description,
        signature = Signature.of("test"),
        build = build,
        promotionLevel = promotionLevel,
    )

}