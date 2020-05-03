package net.nemerosa.ontrack.extension.api

import net.nemerosa.ontrack.model.extension.Extension

/**
 * Contributes to the login page
 */
interface UILoginExtension : Extension {

    /**
     * List of contributions
     */
    val contributions: List<UILogin>

}