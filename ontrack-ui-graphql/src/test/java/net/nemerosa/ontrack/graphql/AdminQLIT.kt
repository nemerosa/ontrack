package net.nemerosa.ontrack.graphql

import com.fasterxml.jackson.databind.JsonNode
import net.nemerosa.ontrack.extension.ldap.LDAPAuthenticationSourceProvider
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.TokensService
import net.nemerosa.ontrack.test.TestUtils.uid
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.access.AccessDeniedException
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AdminQLIT : AbstractQLKTITSupport() {

    @Autowired
    private lateinit var mappingService: AccountGroupMappingService

    @Autowired
    private lateinit var tokensService: TokensService

    private val JsonNode.name: String? get() = get("name").asText()
    private val JsonNode.id: Int? get() = get("id").asInt()

    @Test(expected = AccessDeniedException::class)
    fun `List of groups needs authorisation`() {
        run("""{ accountGroups { id } }""")
    }

    @Test(expected = AccessDeniedException::class)
    fun `List of accounts needs authorisation`() {
        run("""{ accounts { id } }""")
    }

    @Test
    fun `List of groups`() {
        asUser().with(AccountGroupManagement::class.java).call {
            val g = accountService.createGroup(AccountGroupInput(uid("G"), "")).id()
            val data = run("""{ accountGroups { id name } }""")
            assertNotNull(data["accountGroups"].find { it["id"].asInt() == g })
        }
    }

    @Test
    fun `Account group by ID`() {
        val g = doCreateAccountGroup()
        val data = asUser().with(AccountGroupManagement::class.java).call {
            run("""{
                accountGroups(id: ${g.id}) {
                    id
                }
            }""")
        }
        assertEquals(1, data["accountGroups"].size())
        assertEquals(g.id(), data["accountGroups"].first()["id"].asInt())
    }

    @Test
    fun `Account group by name`() {
        val g = doCreateAccountGroup()
        val data = asUser().with(AccountGroupManagement::class.java).call {
            run("""{
                accountGroups(name: "${g.name.substring(1)}") {
                    id
                }
            }""")
        }
        assertEquals(g.id(), data["accountGroups"].first()["id"].asInt())
    }

    @Test
    fun `Accounts for a group`() {
        val g = doCreateAccountGroup()
        val a1 = doCreateAccount(g)
        doCreateAccount()
        val a3 = doCreateAccount(g)
        val data = asUser().with(AccountGroupManagement::class.java).call {
            run("""{
                accountGroups(id: ${g.id}) {
                    id
                    accounts {
                        id
                    }
                }
            }""")
        }
        assertEquals(1, data["accountGroups"].size())
        assertEquals(g.id(), data["accountGroups"].first()["id"].asInt())
        assertEquals(setOf(a1.id(), a3.id()), data["accountGroups"].first()["accounts"].map { it["id"].asInt() }.toSet())
    }

    @Test
    fun `List of accounts`() {
        val a = doCreateAccount()
        asUser().with(AccountManagement::class.java).call {
            val data = run("""{ accounts { id } }""")
            assertNotNull(data["accounts"].find { it["id"].asInt() == a.id() })
        }
    }

    @Test
    fun `Account by ID`() {
        val a = doCreateAccount()
        asUser().with(AccountManagement::class.java).call {
            val data = run("""{ accounts(id: ${a.id}) { name } }""")
            assertEquals(a.name, data["accounts"].first().name)
        }
    }

    @Test
    fun `Account by name`() {
        val a = doCreateAccount()
        asUser().with(AccountManagement::class.java).call {
            val data = run("""{ accounts(name: "${a.name.substring(1)}") { id } }""")
            assertEquals(a.id(), data["accounts"].first()["id"].asInt())
        }
    }

    @Test
    fun `Account by group`() {
        val g = doCreateAccountGroup()
        var a = doCreateAccount()
        asUser().with(AccountManagement::class.java).call {
            a = accountService.updateAccount(a.id, AccountInput(
                    a.name,
                    a.fullName,
                    a.email,
                    "",
                    listOf(g.id())
            ))
            val data = run("""{ accounts(group: "${g.name}") { id }}""")
            assertEquals(a.id(), data["accounts"].first()["id"].asInt())
        }
    }

    @Test
    fun `Account groups`() {
        val g1 = doCreateAccountGroup()
        val g2 = doCreateAccountGroup()
        var a = doCreateAccount()
        asUser().with(AccountManagement::class.java).call {
            a = accountService.updateAccount(a.id, AccountInput(
                    a.name,
                    a.fullName,
                    a.email,
                    "",
                    listOf(g1.id(), g2.id())
            ))
            val data = run("""{ accounts(id: ${a.id}) { groups { name } } }""")
            assertEquals(listOf(g1.name, g2.name), data["accounts"].first()["groups"].map { it.name })
        }
    }

    @Test
    fun `Account without global role`() {
        val a = doCreateAccount()
        val data = asUser().with(AccountManagement::class.java).call {
            run("""{
                accounts(id:${a.id}) {
                    globalRole {
                        id name
                    }
                }
            }""")
        }
        assertTrue(data["accounts"].first()["globalRole"].isNull)
    }

    @Test
    fun `Account global role`() {
        val a = doCreateAccountWithGlobalRole("CONTROLLER")
        val data = asUser().with(AccountManagement::class.java).call {
            run("""{
                accounts(id:${a.id}) {
                    globalRole {
                        id name
                    }
                }
            }""")
        }
        assertEquals("CONTROLLER", data["accounts"].first()["globalRole"]["id"].asText())
        assertEquals("Controller", data["accounts"].first()["globalRole"].name)
    }

    @Test
    fun `Account authorized projects`() {
        val p1 = doCreateProject()
        val p2 = doCreateProject()
        val a = doCreateAccount()
        val data = asAdmin().call {
            accountService.saveProjectPermission(
                    p1.id,
                    PermissionTargetType.ACCOUNT,
                    a.id(),
                    PermissionInput("PARTICIPANT")
            )
            accountService.saveProjectPermission(
                    p2.id,
                    PermissionTargetType.ACCOUNT,
                    a.id(),
                    PermissionInput("OWNER")
            )
            run("""{
                accounts(id: ${a.id}) {
                    authorizedProjects {
                        role {
                            id
                        }
                        project {
                            name
                        }
                    }
                }
            }""")
        }
        val projects = data["accounts"].first()["authorizedProjects"]
        assertEquals(2, projects.size())

        assertEquals("PARTICIPANT", projects.get(0)["role"]["id"].asText())
        assertEquals(p1.name, projects.get(0)["project"].name)

        assertEquals("OWNER", projects.get(1)["role"]["id"].asText())
        assertEquals(p2.name, projects.get(1)["project"].name)
    }

    @Test
    fun `Account group global role`() {
        val g = doCreateAccountGroupWithGlobalRole("CONTROLLER")
        val data = asUser()
                .with(AccountManagement::class.java)
                .with(AccountGroupManagement::class.java).call {
                    run("""{
                accountGroups(id: ${g.id}) {
                    globalRole {
                        id
                        name
                    }
                }
            }""")
                }
        assertEquals("CONTROLLER", data["accountGroups"].first()["globalRole"]["id"].asText())
        assertEquals("Controller", data["accountGroups"].first()["globalRole"].name)
    }

    @Test
    fun `Account group authorized projects`() {
        val p1 = doCreateProject()
        val p2 = doCreateProject()
        val g = doCreateAccountGroup()
        val data = asAdmin().call {
            accountService.saveProjectPermission(
                    p1.id,
                    PermissionTargetType.GROUP,
                    g.id(),
                    PermissionInput("PARTICIPANT")
            )
            accountService.saveProjectPermission(
                    p2.id,
                    PermissionTargetType.GROUP,
                    g.id(),
                    PermissionInput("OWNER")
            )
            run("""{
                accountGroups(id: ${g.id}) {
                    authorizedProjects {
                        role {
                            id
                        }
                        project {
                            name
                        }
                    }
                }
            }""")
        }
        val projects = data["accountGroups"].first()["authorizedProjects"]
        assertEquals(2, projects.size())

        assertEquals("PARTICIPANT", projects.get(0)["role"]["id"].asText())
        assertEquals(p1.name, projects.get(0)["project"].name)

        assertEquals("OWNER", projects.get(1)["role"]["id"].asText())
        assertEquals(p2.name, projects.get(1)["project"].name)
    }

    @Test
    fun `Account group mappings`() {
        val mappingName = uid("M")
        val group = doCreateAccountGroup()
        asAdmin().execute {
            val mapping = mappingService.newMapping(
                    LDAPAuthenticationSourceProvider.SOURCE,
                    AccountGroupMappingInput(
                            mappingName,
                            group.id
                    )
            )
            val data = run("""{
                accountGroups (id: ${group.id}) {
                    mappings {
                        id
                        authenticationSource {
                            provider
                        }
                        name
                    }
                }
            }""")
            val g = data["accountGroups"].first()
            val mappings = g["mappings"]
            assertEquals(1, mappings.size())
            assertEquals(mapping.id(), mappings.first()["id"].asInt())
            assertEquals("ldap", mappings.first()["authenticationSource"]["provider"].asText())
            assertEquals(mappingName, mappings.first().name)
        }
    }

    @Test
    fun `Account group filtered by mapping`() {
        val mappingName = uid("M")
        val group1 = doCreateAccountGroup()
        doCreateAccountGroup()
        doCreateAccountGroup()
        asAdmin().execute {
            mappingService.newMapping(
                    LDAPAuthenticationSourceProvider.SOURCE,
                    AccountGroupMappingInput(
                            mappingName,
                            group1.id
                    )
            )
            val data = run("""{
                accountGroups (mapping: "$mappingName") {
                    id
                }
            }""")
            assertEquals(setOf(group1.id()), data["accountGroups"].map { it.id }.toSet())
        }
    }

    @Test
    fun `List of mappings`() {
        val mappingName1 = uid("M")
        val mappingName2 = uid("M")
        val group1 = doCreateAccountGroup()
        asAdmin().execute {
            mappingService.newMapping(
                    LDAPAuthenticationSourceProvider.SOURCE,
                    AccountGroupMappingInput(
                            mappingName1,
                            group1.id
                    )
            )
            mappingService.newMapping(
                    LDAPAuthenticationSourceProvider.SOURCE,
                    AccountGroupMappingInput(
                            mappingName2,
                            group1.id
                    )
            )
            val data = run("""{
                accountGroupMappings(provider: "ldap") {
                    name
                    authenticationSource {
                        provider
                    }
                    group {
                        id
                    }
                }
            }""")
            val mapping1 = data["accountGroupMappings"].find { it.name == mappingName1 }
            assertNotNull(mapping1)
            assertEquals("ldap", mapping1["authenticationSource"]["provider"].asText())
            assertEquals(group1.id(), mapping1["group"]["id"].asInt())
            val mapping2 = data["accountGroupMappings"].find { it.name == mappingName2 }
            assertNotNull(mapping2)
            assertEquals("ldap", mapping2["authenticationSource"]["provider"].asText())
            assertEquals(group1.id(), mapping2["group"]["id"].asInt())
        }
    }

    @Test
    fun `List of mappings filter by name`() {
        val mappingName1 = uid("M")
        val mappingName2 = uid("M")
        val group1 = doCreateAccountGroup()
        asAdmin().execute {
            mappingService.newMapping(
                    LDAPAuthenticationSourceProvider.SOURCE,
                    AccountGroupMappingInput(
                            mappingName1,
                            group1.id
                    )
            )
            mappingService.newMapping(
                    LDAPAuthenticationSourceProvider.SOURCE,
                    AccountGroupMappingInput(
                            mappingName2,
                            group1.id
                    )
            )
            val data = run("""{
                accountGroupMappings(provider: "ldap", name: "$mappingName1") {
                    name
                    authenticationSource {
                        provider
                    }
                    group {
                        id
                    }
                }
            }""")
            val mapping1 = data["accountGroupMappings"].find { it.name == mappingName1 }
            assertNotNull(mapping1)
            assertEquals("ldap", mapping1["authenticationSource"]["provider"].asText())
            assertEquals(group1.id(), mapping1["group"]["id"].asInt())
            val mapping2 = data["accountGroupMappings"].find { it.name == mappingName2 }
            assertEquals(null, mapping2)
        }
    }

    @Test
    fun `List of mappings filter by group`() {
        val mappingName1 = uid("M")
        val mappingName2 = uid("M")
        val group1 = doCreateAccountGroup()
        val group2 = doCreateAccountGroup()
        asAdmin().execute {
            mappingService.newMapping(
                    LDAPAuthenticationSourceProvider.SOURCE,
                    AccountGroupMappingInput(
                            mappingName1,
                            group1.id
                    )
            )
            mappingService.newMapping(
                    LDAPAuthenticationSourceProvider.SOURCE,
                    AccountGroupMappingInput(
                            mappingName2,
                            group2.id
                    )
            )
            val data = run("""{
                accountGroupMappings(provider: "ldap", group: "${group1.name}") {
                    name
                    authenticationSource {
                        provider
                    }
                    group {
                        id
                    }
                }
            }""")
            val mapping1 = data["accountGroupMappings"].find { it.name == mappingName1 }
            assertNotNull(mapping1)
            assertEquals("ldap", mapping1["authenticationSource"]["provider"].asText())
            assertEquals(group1.id(), mapping1["group"]["id"].asInt())
            val mapping2 = data["accountGroupMappings"].find { it.name == mappingName2 }
            assertEquals(null, mapping2)
        }
    }

    @Test
    fun `Global roles and associated accounts and groups`() {
        val controllerGroup = doCreateAccountGroupWithGlobalRole("CONTROLLER")
        val controllerInGroup = doCreateAccount(controllerGroup)
        val directController = doCreateAccountWithGlobalRole("CONTROLLER")
        asAdmin().execute {
            val data = run("""{
                globalRoles {
                    id
                    groups {
                        id
                        accounts {
                            id
                        }
                    }
                    accounts {
                        id
                    }
                }
            }""")
            assertNotNull(data["globalRoles"].find { it["id"].asText() == "ADMINISTRATOR" })
            assertNotNull(data["globalRoles"].find { it["id"].asText() == "CONTROLLER" }) { controllerRole ->
                val g = controllerRole["groups"].find { it.id == controllerGroup.id() }
                assertNotNull(g) { group ->
                    assertNotNull(group["accounts"].find { it.id == controllerInGroup.id() })
                }
                assertNotNull(controllerRole["accounts"].find { it.id == directController.id() })
            }
        }
    }

    @Test
    fun `Global roles and associated accounts and groups, filtered by role`() {
        val controllerGroup = doCreateAccountGroupWithGlobalRole("CONTROLLER")
        val controllerInGroup = doCreateAccount(controllerGroup)
        val directController = doCreateAccountWithGlobalRole("CONTROLLER")
        asAdmin().execute {
            val data = run("""{
                globalRoles(role: "CONTROLLER") {
                    id
                    groups {
                        id
                        accounts {
                            id
                        }
                    }
                    accounts {
                        id
                    }
                }
            }""")
            assertNull(data["globalRoles"].find { it["id"].asText() == "ADMINISTRATOR" })
            assertNotNull(data["globalRoles"].find { it["id"].asText() == "CONTROLLER" }) { controllerRole ->
                val g = controllerRole["groups"].find { it.id == controllerGroup.id() }
                assertNotNull(g) { group ->
                    assertNotNull(group["accounts"].find { it.id == controllerInGroup.id() })
                }
                assertNotNull(controllerRole["accounts"].find { it.id == directController.id() })
            }
        }
    }

    @Test
    fun `Project roles from the project`() {
        val participantGroup = doCreateAccountGroup()
        val participantInGroup = doCreateAccount(participantGroup)
        val directOwner = doCreateAccount()
        // Project and authorisations
        val project = doCreateProject()
        asAdmin().execute {
            accountService.saveProjectPermission(project.id, PermissionTargetType.GROUP, participantGroup.id(), PermissionInput.of("PARTICIPANT"))
            accountService.saveProjectPermission(project.id, PermissionTargetType.ACCOUNT, directOwner.id(), PermissionInput.of("OWNER"))
            // Query
            val data = run("""{
                projects(id: ${project.id}) {
                    name
                    projectRoles {
                        id
                        groups {
                            id
                            accounts {
                                id
                            }
                        }
                        accounts {
                            id
                        }
                    }
                }
            }""")
            // Checks
            val p = data["projects"].first()
            assertEquals(project.name, p.name)
            // Owner
            val owner = p["projectRoles"].find { it["id"].asText() == "OWNER" }
            assertNotNull(owner) {
                assertTrue(it["groups"].isEmpty)
                assertEquals(directOwner.id(), it["accounts"].first()["id"].asInt())
            }
            // Participant
            val participant = p["projectRoles"].find { it["id"].asText() == "PARTICIPANT" }
            assertNotNull(participant) {
                assertEquals(participantGroup.id(), it["groups"].first()["id"].asInt())
                assertEquals(participantInGroup.id(), it["groups"].first()["accounts"].first()["id"].asInt())
                assertTrue(it["accounts"].isEmpty)
            }
            // Other role
            assertNotNull(p["projectRoles"].find { it["id"].asText() == "VALIDATION_MANAGER" })
        }
    }

    @Test
    fun `Project roles from the project filtered by role`() {
        val participantGroup = doCreateAccountGroup()
        doCreateAccount(participantGroup)
        val directOwner = doCreateAccount()
        // Project and authorisations
        val project = doCreateProject()
        asAdmin().execute {
            accountService.saveProjectPermission(project.id, PermissionTargetType.GROUP, participantGroup.id(), PermissionInput.of("PARTICIPANT"))
            accountService.saveProjectPermission(project.id, PermissionTargetType.ACCOUNT, directOwner.id(), PermissionInput.of("OWNER"))
            // Query
            val data = run("""{
                projects(id: ${project.id}) {
                    name
                    projectRoles(role: "OWNER") {
                        id
                        groups {
                            id
                            accounts {
                                id
                            }
                        }
                        accounts {
                            id
                        }
                    }
                }
            }""")
            // Checks
            val p = data["projects"].first()
            assertEquals(project.name, p.name)
            // Owner
            val owner = p["projectRoles"].find { it["id"].asText() == "OWNER" }
            assertNotNull(owner) {
                assertTrue(it["groups"].isEmpty)
                assertEquals(directOwner.id(), it["accounts"].first()["id"].asInt())
            }
            // Participant
            assertEquals(null, p["projectRoles"].find { it["id"].asText() == "PARTICIPANT" })
            // Other role
            assertEquals(null, p["projectRoles"].find { it["id"].asText() == "VALIDATION_MANAGER" })
        }
    }

    @Test
    fun `Account token not filled in when not generated`() {
        asUser {
            val id = securityService.currentAccount!!.id()
            asAdmin {
                val data = run("""{
                    accounts(id: $id) {
                        token {
                            creation
                            validUntil
                            valid
                        }
                    }
                }""")
                val token = data["accounts"][0]["token"]
                assertTrue(token.isNull)
            }
        }
    }

    @Test
    fun `Account token filled in when generated`() {
        asUser {
            tokensService.generateNewToken()
            val id = securityService.currentAccount!!.id()
            asAdmin {
                val data = run("""{
                    accounts(id: $id) {
                        token {
                            creation
                            validUntil
                            valid
                        }
                    }
                }""")
                val token = data["accounts"][0]["token"]
                assertTrue(token["valid"].booleanValue())
            }
        }
    }

}
