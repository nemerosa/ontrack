package net.nemerosa.ontrack.extension.casc.context.core.admin

import net.nemerosa.ontrack.extension.casc.AbstractCascTestSupport
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseAsJson
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import net.nemerosa.ontrack.model.security.PermissionInput
import net.nemerosa.ontrack.model.security.PermissionTargetType
import net.nemerosa.ontrack.model.security.Roles
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

@Disabled("flaky")
class ProjectPermissionsAdminContextIT : AbstractCascTestSupport() {

    @Autowired
    private lateinit var context: ProjectPermissionsAdminContext

    @Autowired
    private lateinit var jsonTypeBuilder: JsonTypeBuilder

    @Test
    fun `CasC schema type`() {
        val type = context.jsonType(jsonTypeBuilder)
        assertEquals(
            """
                {
                  "items": {
                    "title": "ProjectPermission",
                    "description": null,
                    "properties": {
                      "group": {
                        "description": "Name of the group",
                        "type": "string"
                      },
                      "projects": {
                        "items": {
                          "description": "List of projects",
                          "type": "string"
                        },
                        "description": "List of projects",
                        "type": "array"
                      },
                      "role": {
                        "description": "Name of the role to assign",
                        "type": "string"
                      }
                    },
                    "required": [
                      "group",
                      "projects",
                      "role"
                    ],
                    "additionalProperties": false,
                    "type": "object"
                  },
                  "description": "List of permissions per group",
                  "type": "array"
                }
            """.trimIndent().parseAsJson(),
            type.asJson()
        )
    }

    @Test
    fun `Rendering of permissions`() {
        asAdmin {
            // Deletes existing groups
            accountService.accountGroups.forEach { group ->
                accountService.deleteGroup(group.id)
            }
            // Deletes existing projects
            structureService.projectList.forEach { project ->
                structureService.deleteProject(project.id)
            }
            // Existing items
            val p1 = project()
            val p2 = project()
            val p3 = project()
            val group1 = doCreateAccountGroup()
            val group2 = doCreateAccountGroup()
            val group3 = doCreateAccountGroup()
            // Different roles per project
            accountService.saveProjectPermission(
                p1.id,
                PermissionTargetType.GROUP,
                group1.id(),
                PermissionInput(Roles.PROJECT_OWNER)
            )
            accountService.saveProjectPermission(
                p3.id,
                PermissionTargetType.GROUP,
                group1.id(),
                PermissionInput(Roles.PROJECT_OWNER)
            )
            accountService.saveProjectPermission(
                p1.id,
                PermissionTargetType.GROUP,
                group2.id(),
                PermissionInput(Roles.PROJECT_VALIDATION_MANAGER)
            )
            accountService.saveProjectPermission(
                p2.id,
                PermissionTargetType.GROUP,
                group3.id(),
                PermissionInput(Roles.PROJECT_OWNER)
            )
            // Rendering
            assertEquals(
                listOf(
                    mapOf(
                        "group" to group1.name,
                        "role" to "OWNER",
                        "projects" to listOf(
                            p1.name,
                            p3.name,
                        )
                    ),
                    mapOf(
                        "group" to group2.name,
                        "role" to "VALIDATION_MANAGER",
                        "projects" to listOf(
                            p1.name,
                        )
                    ),
                    mapOf(
                        "group" to group3.name,
                        "role" to "OWNER",
                        "projects" to listOf(
                            p2.name,
                        )
                    ),
                ).asJson(),
                context.render()
            )
        }
    }

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