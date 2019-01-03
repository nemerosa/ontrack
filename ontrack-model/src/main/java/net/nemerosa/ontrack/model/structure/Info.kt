package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.extension.ExtensionList

/**
 * Information about the application.
 *
 * @property version Version of the application.
 * @property extensionList List of extensions
 */
class Info(
        val version: VersionInfo,
        val extensionList: ExtensionList
)
