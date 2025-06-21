package net.nemerosa.ontrack.extension.indicators.acl

import net.nemerosa.ontrack.model.labels.ProjectLabelManagement
import net.nemerosa.ontrack.model.security.*
import org.springframework.stereotype.Component

/**
 * Links this extension's security functions to roles
 */
@Component
class IndicatorRoleContributor : RoleContributor {

    companion object {
        /**
         * Indicator management at project level
         */
        const val PROJECT_INDICATOR_MANAGER = "PROJECT_INDICATOR_MANAGER"

        /**
         * Indicator management at global level
         */
        const val GLOBAL_INDICATOR_MANAGER = "GLOBAL_INDICATOR_MANAGER"

    }

    override fun getProjectRoles(): List<RoleDefinition> =
        listOf(
            RoleDefinition(
                id = PROJECT_INDICATOR_MANAGER,
                name = "Project indicator manager",
                description = "Can manage the indicators of a project, by editing or removing indicator values"
            )
        )

    override fun getGlobalRoles(): List<RoleDefinition> =
        listOf(
            RoleDefinition(
                id = GLOBAL_INDICATOR_MANAGER,
                name = "Global indicator manager",
                description = "Can manage and import indicator categories & types. Can manage portfolios. Can manage indicators in all projects."
            ),
        )

    override fun getGlobalFunctionContributionsForGlobalRoles(): Map<String, List<Class<out GlobalFunction>>> {
        val map = mutableMapOf<String, MutableList<Class<out GlobalFunction>>>()

        Roles.GLOBAL_ROLES.associateWithTo(map) { mutableListOf(IndicatorPortfolioAccess::class.java) }

        map[Roles.GLOBAL_ADMINISTRATOR]?.addAll(
            listOf(
                IndicatorPortfolioIndicatorManagement::class.java,
                IndicatorPortfolioManagement::class.java,
                IndicatorPortfolioAccess::class.java,
                IndicatorTypeManagement::class.java,
                IndicatorViewManagement::class.java
            )
        )

        map[GLOBAL_INDICATOR_MANAGER] = mutableListOf(
            IndicatorPortfolioIndicatorManagement::class.java,
            IndicatorPortfolioManagement::class.java,
            IndicatorPortfolioAccess::class.java,
            IndicatorTypeManagement::class.java,
            IndicatorViewManagement::class.java
        )

        return map
    }

    override fun getProjectFunctionContributionsForGlobalRoles(): Map<String, List<Class<out ProjectFunction>>> =
        mapOf(
            Roles.GLOBAL_ADMINISTRATOR to listOf(
                IndicatorEdit::class.java
            ),
            GLOBAL_INDICATOR_MANAGER to listOf(
                IndicatorEdit::class.java,
                ProjectLabelManagement::class.java
            ),
        )

    override fun getProjectFunctionContributionsForProjectRoles(): Map<String, List<Class<out ProjectFunction>>> =
        mapOf(
            Roles.PROJECT_MANAGER to listOf(
                IndicatorEdit::class.java
            ),
            Roles.PROJECT_OWNER to listOf(
                IndicatorEdit::class.java
            ),
            PROJECT_INDICATOR_MANAGER to listOf(
                IndicatorEdit::class.java
            )
        )
}
