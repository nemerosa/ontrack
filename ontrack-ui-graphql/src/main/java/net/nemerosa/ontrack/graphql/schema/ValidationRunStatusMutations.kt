package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.exceptions.ValidationRunStatusIDNotAvailableException
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationRun
import org.springframework.stereotype.Component

@Component
class ValidationRunStatusMutations(
    private val structureService: StructureService,
) : TypedMutationProvider() {
    override val mutations: List<Mutation> = listOf(
        simpleMutation(
            name = "changeValidationRunStatusComment",
            description = "Change the comment on a validation run status",
            input = ChangeValidationRunStatusCommentInput::class,
            outputName = "validationRun",
            outputType = ValidationRun::class,
            outputDescription = "Updated validation run",
        ) { input ->
            // Gets the parent run
            val runStatusId = ID.of(input.validationRunStatusId)
            val run = structureService.getParentValidationRun(runStatusId)
                ?: throw ValidationRunStatusIDNotAvailableException(runStatusId)
            // Edits the comment
            structureService.saveValidationRunStatusComment(
                run,
                runStatusId,
                input.comment ?: ""
            )
        }
    )
}

data class ChangeValidationRunStatusCommentInput(
    val validationRunStatusId: Int,
    val comment: String?,
)
