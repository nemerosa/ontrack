package net.nemerosa.ontrack.service.security

import net.nemerosa.ontrack.model.labels.LabelManagement
import net.nemerosa.ontrack.model.labels.ProjectLabelManagement
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.support.StartupService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Management of the roles and functions.
 *
 * @property roleContributors Role contributors
 */
@Service
@Transactional
class RolesServiceImpl(
        private val roleContributors: List<RoleContributor>
) : RolesService, StartupService {

    /**
     * Index of global roles
     */
    private val globalRolesIndex = mutableMapOf<String, GlobalRole>()

    /**
     * Index of project roles
     */
    private val projectRolesIndex = mutableMapOf<String, ProjectRole>()

    override val globalRoles: List<GlobalRole>
        get() = globalRolesIndex.values.toList()

    override fun getGlobalRole(id: String): Optional<GlobalRole> {
        return Optional.ofNullable(globalRolesIndex[id])
    }

    override val projectRoles: List<ProjectRole>
        get() = projectRolesIndex.values.toList()

    override fun getProjectRole(id: String): Optional<ProjectRole> {
        return Optional.ofNullable(projectRolesIndex[id])
    }

    override val globalFunctions: List<Class<out GlobalFunction>>
        get() = RolesService.defaultGlobalFunctions

    override val projectFunctions: List<Class<out ProjectFunction>>
        get() = RolesService.defaultProjectFunctions

    override fun getProjectRoleAssociation(project: Int, roleId: String): Optional<ProjectRoleAssociation> {
        return getProjectRole(roleId).map { role -> ProjectRoleAssociation(project, role) }
    }

    override fun getName(): String {
        return "Roles"
    }

    override fun startupOrder(): Int {
        return 50
    }

    override fun start() {
        // Global roles
        initGlobalRoles()
        // Project roles
        initProjectRoles()
    }

    private fun initProjectRoles() {

        // Owner
        registerProjectRole(Roles.PROJECT_OWNER, "Project owner",
                "The project owner is allowed to all functions in a project, but for its deletion.",
                projectFunctions
                        .filter { t -> !ProjectDelete::class.java.isAssignableFrom(t) }
        )

        // Participant
        registerProjectRole(Roles.PROJECT_PARTICIPANT, "Participant",
                "A participant in a project is allowed to change statuses in validation runs.",
                listOf(
                        ProjectView::class.java,
                        ValidationRunStatusChange::class.java,
                        ValidationRunStatusCommentEditOwn::class.java,
                        ValidationStampFilterCreate::class.java
                )
        )

        // Validation manager
        val validationManagerFunctions = listOf(
                ValidationStampCreate::class.java,
                ValidationStampEdit::class.java,
                ValidationStampDelete::class.java,
                ValidationRunCreate::class.java,
                ValidationRunStatusChange::class.java,
                ValidationRunStatusCommentEdit::class.java,
                ValidationStampFilterCreate::class.java,
                ValidationStampFilterShare::class.java,
                ValidationStampFilterMgt::class.java
        )
        registerProjectRole(Roles.PROJECT_VALIDATION_MANAGER, "Validation manager",
                "The validation manager can manage the validation stamps.",
                validationManagerFunctions
        )

        // Promoter
        val promoterFunctions = listOf(
                PromotionRunCreate::class.java,
                PromotionRunDelete::class.java,
                ValidationRunStatusChange::class.java
        )
        registerProjectRole(Roles.PROJECT_PROMOTER, "Promoter",
                "The promoter can promote existing builds.",
                promoterFunctions
        )

        // Project manager
        val projectManagerFunctions = ArrayList<Class<out ProjectFunction>>()
        projectManagerFunctions.addAll(validationManagerFunctions)
        projectManagerFunctions.addAll(promoterFunctions)
        projectManagerFunctions.add(BranchFilterMgt::class.java)
        projectManagerFunctions.add(BranchCreate::class.java)
        projectManagerFunctions.add(BranchEdit::class.java)
        projectManagerFunctions.add(BranchDelete::class.java)
        projectManagerFunctions.add(ProjectLabelManagement::class.java)
        registerProjectRole(Roles.PROJECT_MANAGER, "Project manager",
                "The project manager can promote existing builds, manage the validation stamps, " + "manage the shared build filters, manage the branches and edit some properties.",
                projectManagerFunctions
        )

        // Read only on a project
        registerProjectRole(Roles.PROJECT_READ_ONLY, "Read Only",
                "This role grants a read-only access to all components of the projects",
                RolesService.readOnlyProjectFunctions
        )

        // Project roles contributions
        roleContributors.forEach { roleContributor ->
            roleContributor.projectRoles.forEach { roleDefinition ->
                if (Roles.PROJECT_ROLES.contains(roleDefinition.id)) {
                    // Totally illegal - stopping everything
                    throw IllegalStateException("An existing project role cannot be overridden: " + roleDefinition.id)
                } else {
                    registerProjectRole(
                            roleDefinition.id,
                            roleDefinition.name,
                            roleDefinition.description,
                            getProjectFunctionsFromProjectParent(roleDefinition.parent)
                    )
                }
            }
        }

    }

    override fun registerProjectRole(id: String, name: String, description: String, projectFunctions: List<Class<out ProjectFunction>>): ProjectRole {
        val functions = LinkedHashSet(projectFunctions)
        // Contributions
        roleContributors.forEach { roleContributor ->
            roleContributor.projectFunctionContributionsForProjectRoles[id]?.forEach { fn ->
                // Checks if the role is predefined
                if (Roles.PROJECT_ROLES.contains(id)) {
                    // Checks the function as non core
                    checkFunctionForContribution(fn)
                }
                // OK
                functions.add(fn)
            }
        }
        // OK
        return ProjectRole(
                id,
                name,
                description,
                functions
        ).apply { register(this) }
    }

    private fun checkFunctionForContribution(fn: Class<*>) {
        val coreFunction = fn.getDeclaredAnnotation(CoreFunction::class.java)
        if (coreFunction != null) {
            // Totally illegal - stopping everything
            throw IllegalStateException("A core function cannot be added to an existing role.")
        }
    }

    private fun register(projectRole: ProjectRole) {
        projectRolesIndex[projectRole.id] = projectRole

    }

    private fun initGlobalRoles() {

        // Administrator
        // This particular role must have ALL functions
        registerGlobalRole(Roles.GLOBAL_ADMINISTRATOR, "Administrator",
                "An administrator is allowed to do everything in the application.",
                (globalFunctions +
                        roleContributors.flatMap {
                            it.globalFunctionContributionsForGlobalRoles.values.flatten()
                        }
                        ).distinct(),
                (projectFunctions +
                        roleContributors.flatMap {
                            it.projectFunctionContributionsForGlobalRoles.values.flatten() +
                                    it.projectFunctionContributionsForProjectRoles.values.flatten()
                        }
                        ).distinct()
        )

        // Creator
        registerGlobalRole(Roles.GLOBAL_CREATOR, "Creator",
                "A creator is allowed to create new projects and to configure it. Once done, its rights on the " + "project are revoked immediately.",
                listOf(
                        ProjectCreation::class.java,
                        LabelManagement::class.java
                ),
                listOf(
                        // Structure creation functions only
                        ProjectConfig::class.java,
                        BranchCreate::class.java,
                        PromotionLevelCreate::class.java,
                        ValidationStampCreate::class.java
                )
        )

        // Creator
        registerGlobalRole(Roles.GLOBAL_AUTOMATION, "Automation",
                "This role can be assigned to users or groups which must automate Ontrack. It aggregates both the " + "Creator and the Controller roles into one.",
                listOf(
                        ProjectCreation::class.java,
                        AccountGroupManagement::class.java
                ),
                listOf(
                        // Structure creation functions only
                        ProjectConfig::class.java,
                        ProjectAuthorisationMgt::class.java,
                        BranchCreate::class.java,
                        BranchEdit::class.java,
                        PromotionLevelCreate::class.java,
                        PromotionLevelEdit::class.java,
                        ValidationStampCreate::class.java,
                        ValidationStampEdit::class.java,
                        ProjectView::class.java,
                        BuildCreate::class.java,
                        BuildConfig::class.java,
                        PromotionRunCreate::class.java,
                        ValidationRunCreate::class.java
                )
        )

        // Controller
        registerGlobalRole(Roles.GLOBAL_CONTROLLER, "Controller",
                "A controller, is allowed to create builds, promotion runs and validation runs. He can also " + "synchronise templates. This role is typically granted to continuous integration tools.",
                emptyList(),
                listOf(
                        ProjectView::class.java,
                        BuildCreate::class.java,
                        BuildConfig::class.java,
                        PromotionRunCreate::class.java,
                        ValidationRunCreate::class.java
                )
        )

        // Global validation manager
        registerGlobalRole(Roles.GLOBAL_VALIDATION_MANAGER, "Global validation manager",
                "A global validation manager can manage the validation stamps across all projects and edit validation run comments.",
                listOf(
                        ValidationStampBulkUpdate::class.java
                ),
                listOf(
                        ProjectView::class.java,
                        ValidationStampCreate::class.java,
                        ValidationStampEdit::class.java,
                        ValidationStampDelete::class.java,
                        ValidationRunStatusCommentEdit::class.java
                )
        )

        // Read only on all projects
        registerGlobalRole(Roles.GLOBAL_READ_ONLY, "Read Only",
                "This role grants a read-only access to all projects",
                RolesService.readOnlyGlobalFunctions,
                RolesService.readOnlyProjectFunctions
        )

        // Global roles contributions
        roleContributors.forEach { roleContributor ->
            roleContributor.globalRoles.forEach { roleDefinition ->
                if (Roles.GLOBAL_ROLES.contains(roleDefinition.id)) {
                    // Totally illegal - stopping everything
                    throw IllegalStateException("An existing global role cannot be overridden: " + roleDefinition.id)
                } else {
                    registerGlobalRole(
                            roleDefinition.id,
                            roleDefinition.name,
                            roleDefinition.description,
                            getGlobalFunctionsFromGlobalParent(roleDefinition.parent),
                            getProjectFunctionsFromGlobalParent(roleDefinition.parent)
                    )
                }
            }
        }

    }

    private fun getProjectParentRole(parent: String?): ProjectRole? {
        if (parent == null) {
            return null
        } else if (Roles.PROJECT_ROLES.contains(parent)) {
            val parentRole = projectRolesIndex[parent]
            if (parentRole != null) {
                return parentRole
            }
        }
        throw IllegalStateException("$parent role is not a built-in role or is not registered.")
    }

    private fun getGlobalParentRole(parent: String?): GlobalRole? {
        if (parent == null) {
            return null
        } else if (Roles.GLOBAL_ROLES.contains(parent)) {
            val parentRole = globalRolesIndex[parent]
            if (parentRole != null) {
                return parentRole
            }
        }
        throw IllegalStateException("$parent role is not a built-in role or is not registered.")
    }

    private fun getGlobalFunctionsFromGlobalParent(parent: String?): List<Class<out GlobalFunction>> {
        return getGlobalParentRole(parent)
                ?.globalFunctions?.toList()
                ?: listOf()
    }

    private fun getProjectFunctionsFromGlobalParent(parent: String?): List<Class<out ProjectFunction>> {
        return getGlobalParentRole(parent)
                ?.projectFunctions?.toList()
                ?: listOf()
    }

    private fun getProjectFunctionsFromProjectParent(parent: String?): List<Class<out ProjectFunction>> {
        return getProjectParentRole(parent)
                ?.functions?.toList()
                ?: listOf()
    }

    override fun registerGlobalRole(id: String, name: String, description: String, globalFunctions: List<Class<out GlobalFunction>>, projectFunctions: List<Class<out ProjectFunction>>): GlobalRole {
        // Checks the role if not registered yet
        if (globalRolesIndex.containsKey(id)) {
            throw IllegalStateException("Global role $id is already registered.")
        }
        // Global functions and contributions
        val gfns = LinkedHashSet(globalFunctions)
        roleContributors.forEach { roleContributor ->
            roleContributor.globalFunctionContributionsForGlobalRoles[id]?.forEach { fn ->
                if (Roles.GLOBAL_ROLES.contains(id)) {
                    checkFunctionForContribution(fn)
                }
                gfns.add(fn)
            }
        }
        // Project functions
        val pfns = LinkedHashSet(projectFunctions)
        roleContributors.forEach { roleContributor ->
            roleContributor.projectFunctionContributionsForGlobalRoles[id]?.forEach { fn ->
                if (Roles.GLOBAL_ROLES.contains(id)) {
                    checkFunctionForContribution(fn)
                }
                pfns.add(fn)
            }
        }
        // OK
        val globalRole = GlobalRole(
                id,
                name,
                description,
                gfns,
                pfns
        )
        register(globalRole)
        return globalRole
    }

    private fun register(role: GlobalRole) {
        globalRolesIndex[role.id] = role
    }
}
