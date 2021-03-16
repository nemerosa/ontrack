package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.graphql.support.TypeRef
import net.nemerosa.ontrack.graphql.support.TypedMutationProvider
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.exceptions.BranchNotFoundException
import net.nemerosa.ontrack.model.exceptions.InputException
import net.nemerosa.ontrack.model.exceptions.ProjectNotFoundException
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Component
import javax.validation.constraints.NotNull
import javax.validation.constraints.Pattern

@Component
class BuildMutations(
    private val structureService: StructureService,
    private val securityService: SecurityService,
    private val runInfoService: RunInfoService,
) : TypedMutationProvider() {

    override val mutations: List<Mutation> = listOf(
        /**
         * Creating a build
         */
        simpleMutation(
            CREATE_BUILD, "Creates a new build", CreateBuildInput::class,
            "build", "Created build", Build::class
        ) { input ->
            val branch = getBranch(input)
            val build = structureService.newBuild(
                Build.of(
                    branch = branch,
                    nameDescription = NameDescription(input.name, input.description),
                    signature = securityService.currentSignature
                )
            )
            // Run info
            if (input.runInfo != null) {
                runInfoService.setRunInfo(build, input.runInfo)
            }
            // OK
            build
        },
        /**
         * Creating a build or getting it if it already exists
         */
        simpleMutation(
            CREATE_BUILD_OR_GET, "Creates a new build or gets it if it already exists", CreateBuildOrGetInput::class,
            "build", "Created or existing build", Build::class
        ) { input ->
            createBuildOrGet(input)
        }
    )


    private fun createBuildOrGet(input: CreateBuildOrGetInput): Build {
        val branch = getBranch(input)
        val build = structureService.findBuildByName(branch.project.name, branch.name, input.name).getOrNull()
            ?: structureService.newBuild(
                Build.of(
                    branch = branch,
                    nameDescription = NameDescription(input.name, input.description),
                    signature = securityService.currentSignature
                )
            )
        // Run info
        if (input.runInfo != null) {
            runInfoService.setRunInfo(build, input.runInfo)
        }
        // OK
        return build
    }

    companion object {
        const val CREATE_BUILD = "createBuild"
        const val CREATE_BUILD_OR_GET = "createBuildOrGet"
    }

    private fun getBranch(input: BuildInput): Branch {
        return if (input.branchId != null) {
            checkInput(input.projectName.isNullOrBlank(), "Since branchId is provided, projectName is not required.")
            checkInput(input.projectId == null, "Since branchId is provided, projectId is not required.")
            checkInput(input.branchName.isNullOrBlank(), "Since branchId is provided, branchName is not required.")
            structureService.getBranch(ID.of(input.branchId as Int))
        } else {
            checkInput(!input.branchName.isNullOrBlank(), "branchName is required if branchId is not provided")
            val project = if (input.projectId != null) {
                checkInput(input.projectName.isNullOrBlank(),
                    "Since projectId is provided, projectName is not required.")
                structureService.getProject(ID.of(input.projectId!!))
            } else {
                checkInput(!input.projectName.isNullOrBlank(),
                    "When using branchName, projectName is required if projectId is not provided")
                structureService.findProjectByName(input.projectName!!).getOrNull()
                    ?: throw ProjectNotFoundException(input.projectName)
            }
            structureService.findBranchByName(project.name, input.branchName!!).getOrNull()
                ?: throw BranchNotFoundException(project.name, input.branchName)
        }
    }

    private fun checkInput(
        ok: Boolean,
        message: String
    ) {
        if (!ok) {
            throw BuildInputMismatchException(message)
        }
    }

}

class BuildInputMismatchException(message: String) : InputException(message)

interface BuildInput {
    /**
     * Project ID (required unless project name is provided)
     */
    val projectId: Int?

    /**
     * Project Name (required unless project ID is provided)
     */
    val projectName: String?

    /**
     * Branch ID (required unless project & branch names are provided)
     */
    val branchId: Int?

    /**
     * Branch name (required together with project name, unless branch ID is provided)
     */
    val branchName: String?
}

data class CreateBuildInput(
    @APIDescription("Project ID (required unless project name is provided)")
    override val projectId: Int?,
    @APIDescription("Project Name (required unless project ID is provided)")
    override val projectName: String?,
    @APIDescription("Branch ID (required unless project & branch names are provided)")
    override val branchId: Int?,
    @APIDescription("Branch name (required together with project name, unless branch ID is provided)")
    override val branchName: String?,
    @get:NotNull(message = "The name is required.")
    @get:Pattern(regexp = NameDescription.NAME, message = "The name ${NameDescription.NAME_MESSAGE_SUFFIX}")
    @APIDescription("Build name")
    val name: String,
    @APIDescription("Build description")
    val description: String?,
    @APIDescription("Optional run info")
    @TypeRef
    val runInfo: RunInfoInput?,
) : BuildInput

data class CreateBuildOrGetInput(
    @APIDescription("Project ID (required unless project name is provided)")
    override val projectId: Int?,
    @APIDescription("Project Name (required unless project ID is provided)")
    override val projectName: String?,
    @APIDescription("Branch ID (required unless project & branch names are provided)")
    override val branchId: Int?,
    @APIDescription("Branch name (required together with project name, unless branch ID is provided)")
    override val branchName: String?,
    @get:NotNull(message = "The name is required.")
    @get:Pattern(regexp = NameDescription.NAME, message = "The name ${NameDescription.NAME_MESSAGE_SUFFIX}")
    @APIDescription("Build name")
    val name: String,
    @APIDescription("Build description")
    val description: String?,
    @APIDescription("Optional run info")
    @TypeRef
    val runInfo: RunInfoInput?,
) : BuildInput
