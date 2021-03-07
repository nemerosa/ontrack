package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.exceptions.InputException
import net.nemerosa.ontrack.model.exceptions.ProjectNotFoundException
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

@Component
class BranchMutations(
    private val structureService: StructureService,
    private val securityService: SecurityService
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        /**
         * Creating a branch
         */
        simpleMutation(
            CREATE_BRANCH, "Creates a new branch", CreateBranchInput::class,
            "branch", "Created branch", Branch::class
        ) { input ->
            val project = getProject(input)
            structureService.newBranch(
                Branch.of(
                    project = project,
                    nameDescription = input.toNameDescriptionState()
                )
            )
        },
        /**
         * Creating a project or getting it if it already exists
         */
        simpleMutation(
            CREATE_BRANCH_OR_GET, "Creates a new branch or gets it if it already exists", CreateBranchOrGetInput::class,
            "branch", "Created or existing branch", Branch::class
        ) { input ->
            createBranchOrGet(input)
        }
    )


    private fun createBranchOrGet(input: CreateBranchOrGetInput): Branch {
        val project = getProject(input)
        return structureService.findBranchByName(project.name, input.name).getOrNull()
            ?: structureService.newBranch(
                Branch.of(
                    project,
                    input.toNameDescriptionState()
                )
            )
    }

    companion object {
        const val CREATE_BRANCH = "createBranch"
        const val CREATE_BRANCH_OR_GET = "createBranchOrGet"
    }

    private fun getProject(input: BranchInput): Project {
        return if (input.projectId != null) {
            if (!input.projectName.isNullOrBlank()) {
                throw ProjectIdAndNameProvidedException()
            } else {
                structureService.getProject(ID.of(input.projectId as Int))
            }
        } else if (!input.projectName.isNullOrBlank()) {
            structureService.findProjectByName(input.projectName as String).orElseThrow {
                ProjectNotFoundException(input.projectName)
            }
        } else {
            throw ProjectIdOrNameMissingException()
        }
    }

}

class ProjectIdOrNameMissingException : InputException("Project ID or name is required")
class ProjectIdAndNameProvidedException : InputException("Project ID or name is required, not both.")

interface BranchInput {
    /**
     * Project ID (required unless project name is provided)
     */
    val projectId: Int?

    /**
     * Project Name (required unless project ID is provided)
     */
    val projectName: String?
}

data class CreateBranchInput(
    @APIDescription("Project ID (required unless project name is provided)")
    override val projectId: Int?,
    @APIDescription("Project Name (required unless project ID is provided)")
    override val projectName: String?,
    @get:NotNull(message = "The name is required.")
    @get:Pattern(regexp = NameDescription.NAME, message = "The name ${NameDescription.NAME_MESSAGE_SUFFIX}")
    @APIDescription("Branch name")
    val name: String,
    @APIDescription("Branch description")
    val description: String?,
    @APIDescription("Branch state, null for not disabled")
    val disabled: Boolean?
) : BranchInput {
    fun toNameDescriptionState() = NameDescriptionState(
        name = name,
        description = description,
        isDisabled = disabled ?: false
    )
}

data class CreateBranchOrGetInput(
    @APIDescription("Project ID (required unless project name is provided)")
    override val projectId: Int?,
    @APIDescription("Project Name (required unless project ID is provided)")
    override val projectName: String?,
    @get:NotNull(message = "The name is required.")
    @get:Pattern(regexp = NameDescription.NAME, message = "The name ${NameDescription.NAME_MESSAGE_SUFFIX}")
    @APIDescription("Branch name")
    val name: String,
    @APIDescription("Branch description")
    val description: String?,
    @APIDescription("Branch state, null for not disabled")
    val disabled: Boolean?
) : BranchInput {
    fun toNameDescriptionState() = NameDescriptionState(
        name = name,
        description = description,
        isDisabled = disabled ?: false
    )
}