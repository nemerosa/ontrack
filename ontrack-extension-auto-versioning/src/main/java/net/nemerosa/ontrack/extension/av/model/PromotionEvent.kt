package net.nemerosa.ontrack.extension.av.model

import net.nemerosa.ontrack.model.structure.Build

/**
 * Event describing the promotion of a build on a branch
 *
 * @property build Promoted build
 * @property promotion Promotion
 * @property version Version being promoted (label or name of the build, depending on the settings of the source project)
 */
data class PromotionEvent(
        val build: Build,
        val promotion: String,
        val version: String
)
