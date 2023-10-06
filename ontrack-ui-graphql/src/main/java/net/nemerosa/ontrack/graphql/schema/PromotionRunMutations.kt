package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.exceptions.BranchNotFoundException
import net.nemerosa.ontrack.model.exceptions.BuildNotFoundException
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.PromotionRun
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import kotlin.jvm.optionals.getOrNull

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
        },
        unitMutation<DeletePromotionRunInput>(
            name = "deletePromotionRun",
            description = "Deletes an existing promotion run",
        ) { input ->
            structureService.deletePromotionRun(ID.of(input.promotionRunId))
        }
    )

    private fun promote(build: Build, input: PromotionRunInput): PromotionRun {
        val branch = structureService.findBranchByName(
            build.project.name,
            build.branch.name,
        ).getOrNull() ?: throw BranchNotFoundException(
            build.project.name,
            build.branch.name,
        )
        val promotionLevel = structureService.getOrCreatePromotionLevel(
            branch,
            promotionLevelId = null,
            promotionLevelName = input.promotion,
        )
        val signature = securityService.currentSignature.run {
            if (input.dateTime != null) {
                withTime(input.dateTime)
            } else {
                this
            }
        }
        return structureService.newPromotionRun(
            PromotionRun.of(
                build = build,
                promotionLevel = promotionLevel,
                signature = signature,
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
    val dateTime: LocalDateTime?
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
    @APIDescription("Promotion date/time")
    override val dateTime: LocalDateTime?,
    @APIDescription("Promotion description")
    override val description: String?,
) : PromotionRunInput

class CreatePromotionRunByIdInput(
    @APIDescription("Build ID")
    val buildId: Int,
    @APIDescription("Promotion name")
    override val promotion: String,
    @APIDescription("Promotion date/time")
    override val dateTime: LocalDateTime?,
    @APIDescription("Promotion description")
    override val description: String?,
) : PromotionRunInput

class DeletePromotionRunInput(
    @APIDescription("Promotion run ID")
    val promotionRunId: Int,
)
