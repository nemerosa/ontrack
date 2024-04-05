package net.nemerosa.ontrack.extension.av.processing

import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder

/**
 * Facade to the templating, tuned for the needs of the auto-versioning processing.
 */
interface AutoVersioningTemplatingService {

    /**
     * Generates the information needed for the creation of an auto-versioning PR.
     *
     * @param order Auto-versioning order being processed
     * @param currentVersions Map of current versions per target path
     * @return PR info
     */
    fun generatePRInfo(
        order: AutoVersioningOrder,
        currentVersions: Map<String, String>,
    ): AutoVersioningPRInfo

}