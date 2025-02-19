package net.nemerosa.ontrack.extension.environments.security

import net.nemerosa.ontrack.it.AbstractDSLTestSupport
import net.nemerosa.ontrack.model.security.Roles
import org.junit.jupiter.api.Test

class EnvironmentsRoleContributorIT : AbstractDSLTestSupport() {

    @Test
    fun `Environment functions are granted to the Automation global role`() {
        val project = doCreateProject()
        asGlobalRole(Roles.GLOBAL_AUTOMATION) {
            EnvironmentsRoleContributor.globalFunctions.forEach {
                securityService.checkGlobalFunction(it)
            }
            EnvironmentsRoleContributor.projectFunctions.forEach {
                securityService.checkProjectFunction(project, it)
            }
        }
    }

}