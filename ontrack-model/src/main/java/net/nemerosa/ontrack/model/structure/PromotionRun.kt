package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonView
import net.nemerosa.ontrack.model.buildfilter.BuildDiff

@JsonPropertyOrder(alphabetic = true)
open class PromotionRun(
        override val id: ID,
        @JsonView(value = [ProjectStatusView::class, BranchStatusView::class, PromotionView::class, PromotionRun::class, Build::class, PromotionRunView::class])
        val build: Build,
        @JsonView(value = [Build::class, PromotionRun::class, BranchBuildView::class, BuildDiff::class, BuildView::class, Decoration::class])
        val promotionLevel: PromotionLevel,
        override val signature: Signature,
        override val description: String?
) : ProjectEntity {

    override val project: Project
        get() = build.project

    override val projectEntityType: ProjectEntityType = ProjectEntityType.PROMOTION_RUN

    override val entityDisplayName: String
        get() = "Promotion run ${build.branch.project.name}/${build.branch.name}/${build.name}/${promotionLevel.name}"

    fun withId(id: ID) = PromotionRun(id, build, promotionLevel, signature, description)

    companion object {
        @JvmStatic
        fun of(build: Build, promotionLevel: PromotionLevel, signature: Signature, description: String?): PromotionRun =
                PromotionRun(
                        ID.NONE,
                        build,
                        promotionLevel,
                        signature,
                        description
                )
    }


}
