package net.nemerosa.ontrack.extension.casc.context.core.admin

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.model.security.Roles
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ProjectPermissionsAdminContextIT : AbstractCascTestSupport() {

    @Test
    fun `Setting up permissions on two existing projects and one existing group`() {
        asAdmin {
            // Existing items
            val p1 = project()
            val p2 = project()
            val group = doCreateAccountGroup()
            // Running the Casc config
            casc(
                """
                ontrack:
                    admin:
                        project-permissions:
                            - group: ${group.name}
                              role: ${Roles.PROJECT_OWNER}
                              projects:
                                - ${p1.name}
                                - ${p2.name}
                """.trimIndent()
            )
            // Checks the permissions have been set
            val associations = accountService.getProjectPermissionsForAccountGroup(group).associate {
                it.projectId to it.projectRole
            }
            assertEquals(Roles.PROJECT_OWNER, associations[p1.id()]?.id)
            assertEquals(Roles.PROJECT_OWNER, associations[p2.id()]?.id)
        }
    }

    @Test
    fun `Setting up permissions on one existing project, one non-existing project and one existing group`() {
        asAdmin {
            // Existing items
            val p1 = project()
            val p2 = uid("p")
            val group = doCreateAccountGroup()
            // Running the Casc config
            casc(
                """
                ontrack:
                    admin:
                        project-permissions:
                            - group: ${group.name}
                              role: ${Roles.PROJECT_OWNER}
                              projects:
                                - ${p1.name}
                                - $p2
                """.trimIndent()
            )
            // Checks the permissions have been set
            val associations = accountService.getProjectPermissionsForAccountGroup(group).associate {
                it.projectId to it.projectRole
            }
            assertEquals(1, associations.size)
            assertEquals(Roles.PROJECT_OWNER, associations[p1.id()]?.id)
        }
    }

    @Test
    fun `Setting up permissions on one existing project and a non existing group`() {
        asAdmin {
            // Existing items
            val p1 = project()
            val group = uid("g")
            // Running the Casc config
            casc(
                """
                ontrack:
                    admin:
                        project-permissions:
                            - group: $group
                              role: ${Roles.PROJECT_OWNER}
                              projects:
                                - ${p1.name}
                """.trimIndent()
            )
            // Checks the permissions have been set
            val associations = accountService.getProjectPermissions(p1.id)
            assertEquals(0, associations.size)
        }
    }

    @Test
    fun `Setting up permissions on two existing projects and one existing group and a non existing role`() {
        asAdmin {
            // Existing items
            val p1 = project()
            val p2 = project()
            val group = doCreateAccountGroup()
            // Running the Casc config
            casc(
                """
                ontrack:
                    admin:
                        project-permissions:
                            - group: ${group.name}
                              role: xxxx
                              projects:
                                - ${p1.name}
                                - ${p2.name}
                """.trimIndent()
            )
            // Checks the permissions have been set
            val associations = accountService.getProjectPermissionsForAccountGroup(group).associate {
                it.projectId to it.projectRole
            }
            assertEquals(0, associations.size)
        }
    }

    @Test
    fun `Existing permissions are preserved`() {
        asAdmin {
            // Existing items
            val p1 = project()
            val p2 = project()
            val group = doCreateAccountGroup()
            // Running the Casc config (first run)
            casc(
                """
                ontrack:
                    admin:
                        project-permissions:
                            - group: ${group.name}
                              role: ${Roles.PROJECT_OWNER}
                              projects:
                                - ${p1.name}
                                - ${p2.name}
                """.trimIndent()
            )
            // Checks the permissions have been set
            var associations = accountService.getProjectPermissionsForAccountGroup(group).associate {
                it.projectId to it.projectRole
            }
            assertEquals(Roles.PROJECT_OWNER, associations[p1.id()]?.id)
            assertEquals(Roles.PROJECT_OWNER, associations[p2.id()]?.id)
            // Running the Casc config (second run)
            casc(
                """
                ontrack:
                    admin:
                        project-permissions:
                            - group: ${group.name}
                              role: ${Roles.PROJECT_OWNER}
                              projects:
                                - ${p1.name}
                """.trimIndent()
            )
            // Checks the permissions have been set
            associations = accountService.getProjectPermissionsForAccountGroup(group).associate {
                it.projectId to it.projectRole
            }
            assertEquals(Roles.PROJECT_OWNER, associations[p1.id()]?.id)
            assertEquals(Roles.PROJECT_OWNER, associations[p2.id()]?.id)
        }
    }

    @Test
    fun `Last permission is taken into account in case of duplicate`() {
        asAdmin {
            // Existing items
            val p1 = project()
            val group = doCreateAccountGroup()
            // Running the Casc config
            casc(
                """
                ontrack:
                    admin:
                        project-permissions:
                            - group: ${group.name}
                              role: ${Roles.PROJECT_OWNER}
                              projects:
                                - ${p1.name}
                            - group: ${group.name}
                              role: ${Roles.PROJECT_VALIDATION_MANAGER}
                              projects:
                                - ${p1.name}
                """.trimIndent()
            )
            // Checks the permissions have been set
            val associations = accountService.getProjectPermissionsForAccountGroup(group).associate {
                it.projectId to it.projectRole
            }
            assertEquals(1, associations.size)
            assertEquals(Roles.PROJECT_VALIDATION_MANAGER, associations[p1.id()]?.id)
        }
    }

}