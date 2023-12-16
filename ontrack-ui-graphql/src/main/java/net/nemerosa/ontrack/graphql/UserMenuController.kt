package net.nemerosa.ontrack.graphql

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.extension.api.UserMenuGroupExtension
import net.nemerosa.ontrack.extension.api.UserMenuItemExtension
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class UserMenuController(
    private val extensionManager: ExtensionManager,
) {

    private val itemsExtensions: Collection<UserMenuItemExtension> by lazy {
        extensionManager.getExtensions(UserMenuItemExtension::class.java)
    }

    private val groupsExtensions: Collection<UserMenuGroupExtension> by lazy {
        extensionManager.getExtensions(UserMenuGroupExtension::class.java)
    }

    @QueryMapping
    fun userMenuItems(): List<APIUserMenuGroup> {
        // Getting all the groups and sorting them
        val groups = groupsExtensions.flatMap { it.groups }.sortedBy { it.name }
        // Getting and indexing all the items by groups
        val groupedItems = itemsExtensions.flatMap { it.items }.groupBy { it.groupId }
        // Consolidating
        return groups.map { group ->
            APIUserMenuGroup(
                id = group.id,
                name = group.name,
                items = groupedItems[group.id]?.map { item ->
                    APIUserMenuItem(
                        extension = item.extension,
                        id = item.id,
                        name = item.name,
                    )
                }?.sortedBy { it.name } ?: emptyList()
            )
        }.filter { it.items.isNotEmpty() }
    }

    data class APIUserMenuGroup(
        val id: String,
        val name: String,
        val items: List<APIUserMenuItem>,
    )

    data class APIUserMenuItem(
        val extension: String,
        val id: String,
        val name: String,
    )

}