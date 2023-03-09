package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.extension.ExtensionList

interface ExtensionManager {

    fun <T : Extension> getExtensions(extensionType: Class<T>): Collection<T>

    /**
     * Gets the list of extensions and the associated dependency graph
     */
    val extensionList: ExtensionList
}