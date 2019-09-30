package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.structure.Build

/**
 * Extension which allows to render the display name for
 * a [Build].
 */
interface BuildDisplayNameExtension : Extension {

    fun getBuildDisplayName(build: Build): String?

}