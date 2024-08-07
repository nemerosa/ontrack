package net.nemerosa.ontrack.extension.av.model

import net.nemerosa.ontrack.model.structure.Build

/**
 * Event describing the promotion of a build on a branch
 *
 * @property build Promoted build
 * @property promotion Promotion
 */
@Deprecated("Use directly the promotion run")
data class PromotionEvent(
        val build: Build,
        val promotion: String,
)
