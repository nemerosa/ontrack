package net.nemerosa.ontrack.model.support

import net.nemerosa.ontrack.model.extension.ExtensionFeature

/**
 * User menu item
 */
data class UserMenuItem(
    val groupId: String,
    val extension: String,
    val id: String,
    val name: String,
) {
    constructor(
        groupId: String,
        extension: ExtensionFeature,
        id: String,
        name: String,
    ) : this(
        groupId = groupId,
        extension = "extension/${extension.id}",
        id = id,
        name = name,
    )
}