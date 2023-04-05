package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension
import net.nemerosa.ontrack.model.support.Action

/**
 * Extension that can return an action.
 */
interface ActionExtension : Extension {
    /**
     * The action to declare.
     */
    val action: Action
}
