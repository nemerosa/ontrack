package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.exceptions.PromotionLevelNotFoundException
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class PromotionRunMutations(
    private val structureService: StructureService,
    private val securityService: SecurityService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        simpleMutation(
            name = CREATE_PROMOTION_RUN_FOR_BUILD_BY_NAME,
            description = "Creating a promotion run for a build identified by its name",
            input = CreatePromotionRunInput::class,
            outputName = "promotionRun",
            outputDescription = "Created promotion run",
            outputType = PromotionRun::class
        ) { input ->
            val build = (structureService.findBuildByName(input.project, input.branch, input.build)
                .getOrNull()
                ?: throw BuildNotFoundException(input.project, input.branch, input.build))
            promote(build, input)
        },
        simpleMutation(
            name = CREATE_PROMOTION_RUN_FOR_BUILD_BY_ID,
            description = "Creating a promotion run for a build identified by its ID",
            input = CreatePromotionRunByIdInput::class,
            outputName = "promotionRun",
            outputDescription = "Created promotion run",
            outputType = PromotionRun::class
        ) { input ->
            val build = structureService.getBuild(ID.of(input.buildId))
            promote(build, input)
        }
    )

    private fun promote(build: Build, input: PromotionRunInput): PromotionRun {
        val promotionLevel = structureService.findPromotionLevelByName(
            build.project.name,
            build.branch.name,
            input.promotion
        ).getOrNull() ?: throw PromotionLevelNotFoundException(
            build.project.name,
            build.branch.name,
            input.promotion
        )
        return structureService.newPromotionRun(
            PromotionRun.of(
                build = build,
                promotionLevel = promotionLevel,
                signature = securityService.currentSignature,
                description = input.description,
            )
        )
    }

    companion object {
        const val CREATE_PROMOTION_RUN_FOR_BUILD_BY_ID = "createPromotionRunById"
        const val CREATE_PROMOTION_RUN_FOR_BUILD_BY_NAME = "createPromotionRun"
    }

}

interface PromotionRunInput {
    val promotion: String
    val description: String?
}

class CreatePromotionRunInput(
    @APIDescription("Project name")
    val project: String,
    @APIDescription("Branch name")
    val branch: String,
    @APIDescription("Build name")
    val build: String,
    @APIDescription("Promotion name")
    override val promotion: String,
    @APIDescription("Promotion description")
    override val description: String?,
) : PromotionRunInput

class CreatePromotionRunByIdInput(
    @APIDescription("Build ID")
    val buildId: Int,
    @APIDescription("Promotion name")
    override val promotion: String,
    @APIDescription("Promotion description")
    override val description: String?,
) : PromotionRunInput
