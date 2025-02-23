package net.nemerosa.ontrack.extension.environments.security

import net.nemerosa.ontrack.model.security.*
import org.springframework.stereotype.Component

@Component
class EnvironmentsRoleContributor : RoleContributor {

    companion object {
        const val GLOBAL_ROLE_ENVIRONMENTS_MANAGER = "GLOBAL_ROLE_ENVIRONMENTS_MANAGER"
        const val PROJECT_ROLE_ENVIRONMENTS_MANAGER = "PROJECT_ROLE_ENVIRONMENTS_MANAGER"
        const val PROJECT_ROLE_PIPELINES_MANAGER = "PROJECT_ROLE_PIPELINES_MANAGER"

        val globalFunctions = listOf(
            EnvironmentList::class.java,
            EnvironmentSave::class.java,
            EnvironmentDelete::class.java,
        )

        val projectFunctions = listOf(
            SlotCreate::class.java,
            SlotUpdate::class.java,
            SlotDelete::class.java,
            SlotView::class.java,
            SlotPipelineCreate::class.java,
            SlotPipelineStart::class.java,
            SlotPipelineCancel::class.java,
            SlotPipelineFinish::class.java,
            SlotPipelineData::class.java,
            SlotPipelineOverride::class.java,
            SlotPipelineWorkflowRun::class.java,
            SlotPipelineDelete::class.java,
        )
    }

    override fun getGlobalRoles(): List<RoleDefinition> = listOf(
        RoleDefinition(
            id = GLOBAL_ROLE_ENVIRONMENTS_MANAGER,
            name = "Global environments manager",
            description = "Can manage environments",
        )
    )

    override fun getProjectRoles(): List<RoleDefinition> = listOf(
        RoleDefinition(
            id = PROJECT_ROLE_ENVIRONMENTS_MANAGER,
            name = "Project environments manager",
            description = "Can manage deployment slots for this project",
        ),
        RoleDefinition(
            id = PROJECT_ROLE_PIPELINES_MANAGER,
            name = "Project pipelines manager",
            description = "Can manage deployment pipelines for this project",
        ),
    )

    override fun getGlobalFunctionContributionsForGlobalRoles(): Map<String, List<Class<out GlobalFunction>>> = mapOf(
        Roles.GLOBAL_ADMINISTRATOR to globalFunctions,
        Roles.GLOBAL_AUTOMATION to globalFunctions,
        Roles.GLOBAL_READ_ONLY to listOf(
            EnvironmentList::class.java,
        ),
        Roles.GLOBAL_PARTICIPANT to listOf(
            EnvironmentList::class.java,
        ),
        GLOBAL_ROLE_ENVIRONMENTS_MANAGER to globalFunctions,
    )

    override fun getProjectFunctionContributionsForGlobalRoles(): Map<String, List<Class<out ProjectFunction>>> = mapOf(
        Roles.GLOBAL_ADMINISTRATOR to projectFunctions,
        Roles.GLOBAL_AUTOMATION to projectFunctions,
        Roles.GLOBAL_PARTICIPANT to listOf(
            SlotView::class.java,
        ),
        Roles.GLOBAL_READ_ONLY to listOf(
            SlotView::class.java,
        ),
        GLOBAL_ROLE_ENVIRONMENTS_MANAGER to projectFunctions,
    )

    override fun getProjectFunctionContributionsForProjectRoles(): Map<String, List<Class<out ProjectFunction>>> =
        mapOf(
            Roles.PROJECT_OWNER to projectFunctions,
            Roles.PROJECT_MANAGER to projectFunctions,
            Roles.PROJECT_PARTICIPANT to listOf(
                SlotView::class.java,
            ),
            Roles.PROJECT_READ_ONLY to listOf(
                SlotView::class.java,
            ),
            PROJECT_ROLE_ENVIRONMENTS_MANAGER to projectFunctions,
            PROJECT_ROLE_PIPELINES_MANAGER to listOf(
                SlotPipelineCreate::class.java,
                SlotPipelineStart::class.java,
                SlotPipelineCancel::class.java,
                SlotPipelineFinish::class.java,
                SlotPipelineData::class.java,
                SlotPipelineOverride::class.java,
                SlotPipelineWorkflowRun::class.java,
            ),
        )
}