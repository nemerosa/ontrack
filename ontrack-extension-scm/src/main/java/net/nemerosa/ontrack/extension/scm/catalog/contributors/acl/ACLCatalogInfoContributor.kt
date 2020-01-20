package net.nemerosa.ontrack.extension.scm.catalog.contributors.acl

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.NullNode
import net.nemerosa.ontrack.extension.scm.SCMExtensionFeature
import net.nemerosa.ontrack.extension.scm.catalog.SCMCatalogEntry
import net.nemerosa.ontrack.extension.scm.catalog.contributors.AbstractCoreCatalogInfoContributor
import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.model.security.*
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Component

@Component
class ACLCatalogInfoContributor(
        private val securityService: SecurityService,
        private val accountService: AccountService,
        extension: SCMExtensionFeature
) : AbstractCoreCatalogInfoContributor<ACLCatalogInfo>(extension) {

    override val name: String = "Ontrack ACL"

    /**
     * Main method to load the ACL of a project.
     */
    private fun loadACL(project: Project): ACLCatalogInfo =
            securityService.callAsAdmin {
                val permissions = accountService.getProjectPermissions(project.id)
                val roleIndex = mutableMapOf<String, ProjectRole>()
                val permissionIndex = mutableMapOf<String, MutableList<PermissionTarget>>()
                permissions.forEach { permission ->
                    val role = permission.role
                    roleIndex.putIfAbsent(role.id, role)
                    permissionIndex.getOrPut(role.id) { mutableListOf() }.add(permission.target)
                }
                val projectRoles = roleIndex.mapNotNull { (roleId, role) ->
                    val targets = permissionIndex[roleId]?.toList()
                    targets?.let { ACLProject(role, it) }
                }
                ACLCatalogInfo(projectRoles)
            }

    /**
     * Just an empty list, we'll always reload the information
     */
    override fun collectInfo(project: Project, entry: SCMCatalogEntry) = ACLCatalogInfo(emptyList())

    override fun asClientJson(info: ACLCatalogInfo): JsonNode = info.asJson()

    /**
     * Always reloading, no need to store anything
     */
    override fun asStoredJson(info: ACLCatalogInfo): JsonNode = NullNode.instance

    /**
     * Reloading all the time
     */
    override fun fromStoredJson(project: Project, node: JsonNode) = loadACL(project)

    /**
     * Dynamic collection
     */
    override val isDynamic: Boolean = true

}

data class ACLCatalogInfo(
        val projectRoles: List<ACLProject>
)

data class ACLProject(
        val role: ProjectRole,
        val targets: List<PermissionTarget>
)