package net.nemerosa.ontrack.extensions.environments.security

import net.nemerosa.ontrack.model.security.*
import org.springframework.stereotype.Component

@Component
class EnvironmentsRoleContributor : RoleContributor {

    companion object {
        const val GLOBAL_ROLE_ENVIRONMENTS_MANAGER = "GLOBAL_ROLE_ENVIRONMENTS_MANAGER"
        const val PROJECT_ROLE_ENVIRONMENTS_MANAGER = "PROJECT_ROLE_ENVIRONMENTS_MANAGER"
        const val PROJECT_ROLE_PIPELINES_MANAGER = "PROJECT_ROLE_PIPELINES_MANAGER"
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
        Roles.GLOBAL_ADMINISTRATOR to listOf(
            EnvironmentList::class.java,
            EnvironmentSave::class.java,
            EnvironmentDelete::class.java,
        ),
        Roles.GLOBAL_READ_ONLY to listOf(
            EnvironmentList::class.java,
        ),
        Roles.GLOBAL_PARTICIPANT to listOf(
            EnvironmentList::class.java,
        ),
        GLOBAL_ROLE_ENVIRONMENTS_MANAGER to listOf(
            EnvironmentList::class.java,
            EnvironmentSave::class.java,
            EnvironmentDelete::class.java,
        ),
    )

    override fun getProjectFunctionContributionsForGlobalRoles(): Map<String, List<Class<out ProjectFunction>>> = mapOf(
        Roles.GLOBAL_ADMINISTRATOR to listOf(
            SlotCreate::class.java,
            SlotUpdate::class.java,
            SlotDelete::class.java,
            SlotView::class.java,
            SlotPipelineCreate::class.java,
            SlotPipelineStart::class.java,
            SlotPipelineCancel::class.java,
            SlotPipelineFinish::class.java,
            SlotPipelineOverride::class.java,
        ),
        Roles.GLOBAL_PARTICIPANT to listOf(
            SlotView::class.java,
        ),
        Roles.GLOBAL_READ_ONLY to listOf(
            SlotView::class.java,
        ),
        GLOBAL_ROLE_ENVIRONMENTS_MANAGER to listOf(
            SlotCreate::class.java,
            SlotUpdate::class.java,
            SlotDelete::class.java,
            SlotView::class.java,
            SlotPipelineCreate::class.java,
            SlotPipelineStart::class.java,
            SlotPipelineCancel::class.java,
            SlotPipelineFinish::class.java,
            SlotPipelineOverride::class.java,
        ),
    )

    override fun getProjectFunctionContributionsForProjectRoles(): Map<String, List<Class<out ProjectFunction>>> =
        mapOf(
            Roles.PROJECT_OWNER to listOf(
                SlotCreate::class.java,
                SlotUpdate::class.java,
                SlotDelete::class.java,
                SlotView::class.java,
                SlotPipelineCreate::class.java,
                SlotPipelineStart::class.java,
                SlotPipelineCancel::class.java,
                SlotPipelineFinish::class.java,
                SlotPipelineOverride::class.java,
            ),
            Roles.PROJECT_MANAGER to listOf(
                SlotCreate::class.java,
                SlotUpdate::class.java,
                SlotDelete::class.java,
                SlotView::class.java,
                SlotPipelineCreate::class.java,
                SlotPipelineStart::class.java,
                SlotPipelineCancel::class.java,
                SlotPipelineFinish::class.java,
                SlotPipelineOverride::class.java,
            ),
            Roles.PROJECT_PARTICIPANT to listOf(
                SlotView::class.java,
            ),
            Roles.PROJECT_READ_ONLY to listOf(
                SlotView::class.java,
            ),
            PROJECT_ROLE_ENVIRONMENTS_MANAGER to listOf(
                SlotCreate::class.java,
                SlotUpdate::class.java,
                SlotDelete::class.java,
                SlotPipelineCreate::class.java,
                SlotPipelineStart::class.java,
                SlotPipelineCancel::class.java,
                SlotPipelineFinish::class.java,
                SlotPipelineOverride::class.java,
            ),
            PROJECT_ROLE_PIPELINES_MANAGER to listOf(
                SlotPipelineCreate::class.java,
                SlotPipelineStart::class.java,
                SlotPipelineCancel::class.java,
                SlotPipelineFinish::class.java,
                SlotPipelineOverride::class.java,
            ),
        )
}