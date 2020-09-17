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
    private val globalRolesIndex = LinkedHashMap<String, GlobalRole>()

    /**
     * Index of project roles
     */
    private val projectRolesIndex = LinkedHashMap<String, ProjectRole>()

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
        register(Roles.PROJECT_OWNER, "Project owner",
                "The project owner is allowed to all functions in a project, but for its deletion.",
                projectFunctions
                        .filter { t -> !ProjectDelete::class.java.isAssignableFrom(t) }
        )

        // Participant
        register(Roles.PROJECT_PARTICIPANT, "Participant",
                "A participant in a project is allowed to change statuses in validation runs.",
                Arrays.asList(
                        ProjectView::class.java,
                        ValidationRunStatusChange::class.java,
                        ValidationRunStatusCommentEditOwn::class.java,
                        ValidationStampFilterCreate::class.java
                )
        )

        // Validation manager
        val validationManagerFunctions = Arrays.asList(
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
        register(Roles.PROJECT_VALIDATION_MANAGER, "Validation manager",
                "The validation manager can manage the validation stamps.",
                validationManagerFunctions
        )

        // Promoter
        val promoterFunctions = Arrays.asList(
                PromotionRunCreate::class.java,
                PromotionRunDelete::class.java,
                ValidationRunStatusChange::class.java
        )
        register(Roles.PROJECT_PROMOTER, "Promoter",
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
        register(Roles.PROJECT_MANAGER, "Project manager",
                "The project manager can promote existing builds, manage the validation stamps, " + "manage the shared build filters, manage the branches and edit some properties.",
                projectManagerFunctions
        )

        // Read only on a project
        register(Roles.PROJECT_READ_ONLY, "Read Only",
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
                    register(
                            roleDefinition.id,
                            roleDefinition.name,
                            roleDefinition.description,
                            getProjectFunctionsFromProjectParent(roleDefinition.parent)
                    )
                }
            }
        }

    }

    private fun register(id: String, name: String, description: String, projectFunctions: List<Class<out ProjectFunction>>) {
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
        register(ProjectRole(
                id,
                name,
                description,
                functions
        ))
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
        register(Roles.GLOBAL_ADMINISTRATOR, "Administrator",
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
        register(Roles.GLOBAL_CREATOR, "Creator",
                "A creator is allowed to create new projects and to configure it. Once done, its rights on the " + "project are revoked immediately.",
                listOf(
                        ProjectCreation::class.java,
                        LabelManagement::class.java
                ),
                listOf(
                        // Structure creation functions only
                        ProjectConfig::class.java,
                        BranchCreate::class.java,
                        BranchTemplateMgt::class.java,
                        PromotionLevelCreate::class.java,
                        ValidationStampCreate::class.java
                )
        )

        // Creator
        register(Roles.GLOBAL_AUTOMATION, "Automation",
                "This role can be assigned to users or groups which must automate Ontrack. It aggregates both the " + "Creator and the Controller roles into one.",
                Arrays.asList(
                        ProjectCreation::class.java,
                        AccountGroupManagement::class.java
                ),
                Arrays.asList(
                        // Structure creation functions only
                        ProjectConfig::class.java,
                        ProjectAuthorisationMgt::class.java,
                        BranchCreate::class.java,
                        BranchTemplateMgt::class.java,
                        PromotionLevelCreate::class.java,
                        PromotionLevelEdit::class.java,
                        ValidationStampCreate::class.java,
                        ValidationStampEdit::class.java,
                        ProjectView::class.java,
                        BuildCreate::class.java,
                        BuildConfig::class.java,
                        PromotionRunCreate::class.java,
                        ValidationRunCreate::class.java,
                        ValidationRunStatusCommentEdit::class.java,
                        BranchTemplateSync::class.java
                )
        )

        // Controller
        register(Roles.GLOBAL_CONTROLLER, "Controller",
                "A controller, is allowed to create builds, promotion runs and validation runs. He can also " + "synchronise templates. This role is typically granted to continuous integration tools.",
                emptyList(),
                Arrays.asList(
                        ProjectView::class.java,
                        BuildCreate::class.java,
                        BuildConfig::class.java,
                        PromotionRunCreate::class.java,
                        ValidationRunCreate::class.java,
                        BranchTemplateSync::class.java
                )
        )

        // Global validation manager
        register(Roles.GLOBAL_VALIDATION_MANAGER, "Global validation manager",
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
        register(Roles.GLOBAL_READ_ONLY, "Read Only",
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
                    register(
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

    private fun register(id: String, name: String, description: String, globalFunctions: List<Class<out GlobalFunction>>, projectFunctions: List<Class<out ProjectFunction>>) {
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
        register(GlobalRole(
                id,
                name,
                description,
                gfns,
                pfns
        ))
    }

    private fun register(role: GlobalRole) {
        globalRolesIndex[role.id] = role
    }
}
