package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.settings.PredefinedPromotionLevelService
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.NameDescription
import net.nemerosa.ontrack.model.structure.PredefinedPromotionLevel
import org.springframework.stereotype.Component

@Component
class PredefinedPromotionLevelMutations(
    private val predefinedPromotionLevelService: PredefinedPromotionLevelService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        simpleMutation(
            name = "createPredefinedPromotionLevel",
            description = "Creates a new predefined promotion level",
            input = CreatePredefinedPromotionLevelInput::class,
            outputName = "predefinedPromotionLevel",
            outputDescription = "Created predefined promotion level",
            outputType = PredefinedPromotionLevel::class
        ) { input ->
            predefinedPromotionLevelService.newPredefinedPromotionLevel(
                PredefinedPromotionLevel.of(
                    NameDescription.nd(
                        name = input.name,
                        description = input.description,
                    )
                )
            )
        },
        simpleMutation(
            name = "updatePredefinedPromotionLevel",
            description = "Updates an existing predefined promotion level",
            input = UpdatePredefinedPromotionLevelInput::class,
            outputName = "predefinedPromotionLevel",
            outputDescription = "Updated predefined promotion level",
            outputType = PredefinedPromotionLevel::class
        ) { input ->
            val id = ID.of(input.id)
            val existing = predefinedPromotionLevelService.getPredefinedPromotionLevel(id)
            val ppl = PredefinedPromotionLevel(
                id = id,
                name = input.name,
                description = input.description,
                isImage = existing.isImage,
            )
            predefinedPromotionLevelService.savePredefinedPromotionLevel(ppl)
            ppl
        },
        unitMutation(
            name = "deletePredefinedPromotionLevel",
            description = "Deletes an existing predefined promotion level",
            input = DeletePredefinedPromotionLevelInput::class,
        ) { input ->
            val id = ID.of(input.id)
            predefinedPromotionLevelService.deletePredefinedPromotionLevel(id)
        },
        /**
         * Reordering predefined promotion levels
         */
        unitMutation<ReorderPredefinedPromotionLevelByIdInput>(
            name = "reorderPredefinedPromotionLevelById",
            description = "Reordering the predefining promotion levels"
        ) { input ->
            predefinedPromotionLevelService.reorderPromotionLevels(
                activeId = input.activeId,
                overId = input.overId,
            )
        }
    )
}

data class CreatePredefinedPromotionLevelInput(
    @APIDescription("Unique name for the predefined promotion level")
    val name: String,
    @APIDescription("Description for the predefined promotion level")
    val description: String,
)

data class UpdatePredefinedPromotionLevelInput(
    @APIDescription("ID of the predefined promotion level")
    val id: Int,
    @APIDescription("Unique name for the predefined promotion level")
    val name: String,
    @APIDescription("Description for the predefined promotion level")
    val description: String,
)

data class DeletePredefinedPromotionLevelInput(
    @APIDescription("ID of the predefined promotion level")
    val id: Int,
)

data class ReorderPredefinedPromotionLevelByIdInput(
    @APIDescription("Active ID")
    val activeId: Int,
    @APIDescription("ID being replaced")
    val overId: Int,
)
