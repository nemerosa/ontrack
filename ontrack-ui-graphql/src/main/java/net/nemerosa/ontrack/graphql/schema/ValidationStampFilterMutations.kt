package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.support.ListRef
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.ValidationStampFilter
import net.nemerosa.ontrack.model.structure.ValidationStampFilterService
import org.springframework.stereotype.Component

@Component
class ValidationStampFilterMutations(
    private val filterService: ValidationStampFilterService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        simpleMutation(
            name = "createValidationStampFilter",
            description = "Creates an empty validation stamp filter",
            input = CreateValidationStampFilterInput::class,
            outputName = "validationStampFilter",
            outputDescription = "Validation stamp filter having been created",
            outputType = ValidationStampFilter::class,
        ) { input ->
            filterService.newValidationStampFilter(
                ValidationStampFilter(
                    name = input.name,
                    vsNames = emptyList(),
                )
            )
        },
        simpleMutation(
            name = "updateValidationStampFilter",
            description = "Updates a validation stamp filter",
            input = UpdateValidationStampFilterInput::class,
            outputName = "validationStampFilter",
            outputDescription = "Validation stamp filter having been updated",
            outputType = ValidationStampFilter::class,
        ) { input ->
            // Loads the existing validation stamp filter
            val vsf = filterService.getValidationStampFilter(ID(input.id))
                // Changes the validations
                .withVsNames(input.vsNames)
            // Update
            filterService.saveValidationStampFilter(vsf)
            // OK
            vsf
        },
        unitMutation(
            name = "deleteValidationStampFilterById",
            description = "Deletes a validation stamp filter",
            input = DeleteValidationStampFilterByIdInput::class,
        ) { input ->
            val vsf = filterService.getValidationStampFilter(ID(input.id))
            filterService.deleteValidationStampFilter(vsf)
        },
    )

}

@APIDescription("Creation of an empty validation stamp filter")
data class CreateValidationStampFilterInput(
    @APIDescription("Name of the validation stamp filter")
    val name: String,
)


@APIDescription("Updating a validation stamp filter")
data class UpdateValidationStampFilterInput(
    @APIDescription("ID of the validation stamp filter")
    val id: Int,
    @APIDescription("Validation stamp names")
    @ListRef
    val vsNames: List<String>,
)

@APIDescription("Deleting a validation stamp filter")
data class DeleteValidationStampFilterByIdInput(
    @APIDescription("ID of the validation stamp filter")
    val id: Int,
)
