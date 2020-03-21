package net.nemerosa.ontrack.extension.scm.catalog

import net.nemerosa.ontrack.model.security.GlobalFunction
import net.nemerosa.ontrack.model.security.RoleContributor
import net.nemerosa.ontrack.model.security.Roles
import org.springframework.stereotype.Component

/**
 * Function needed to access the SCM catalog
 */
interface SCMCatalogAccessFunction : GlobalFunction

/**
 * Grants the [SCMCatalogAccessFunction] to all global roles
 */
@Component
class SCMCatalogACL : RoleContributor {

    override fun getGlobalFunctionContributionsForGlobalRoles(): Map<String, List<Class<out GlobalFunction>>> =
            Roles.GLOBAL_ROLES.associateWith {
                listOf(SCMCatalogAccessFunction::class.java)
            }

}