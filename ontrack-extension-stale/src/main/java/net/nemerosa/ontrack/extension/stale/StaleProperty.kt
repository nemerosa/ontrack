package net.nemerosa.ontrack.extension.stale

import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Defines a stale policy for branches. A branch last activity is the date its last build was created.
 *
 * @property disablingDuration Number of days of inactivity after which the branch is _disabled_
 * @property deletingDuration Number of days of inactivity _after_ a branch has been disabled after which the branch
 *           is _deleted_. If 0, the branches are never deleted.
 * @property promotionsToKeep List of promotions to always keep. If a branch has at least one build having one of these
 *           promotions, the branch will never be disabled not deleted.
 * @property includes Regular expression to identify branches which will never be disabled not deleted
 * @property excludes Can define a regular expression for exceptions to the [includes] rule
 */
data class StaleProperty(
    @APIDescription("Number of days of inactivity after which the branch is disabled")
    val disablingDuration: Int,
    @APIDescription("Number of days of inactivity after a branch has been disabled after which the branch is deleted. If 0, the branches are never deleted.")
    val deletingDuration: Int?,
    @APIDescription("List of promotions to always keep. If a branch has at least one build having one of these promotions, the branch will never be disabled not deleted.")
    val promotionsToKeep: List<String>?,
    @APIDescription("Regular expression to identify branches which will never be disabled not deleted")
    val includes: String?,
    @APIDescription("Can define a regular expression for exceptions to the includes rule")
    val excludes: String?,
) {

    fun withDisablingDuration(disablingDuration: Int): StaleProperty {
        return if (this.disablingDuration == disablingDuration) this else StaleProperty(
            disablingDuration,
            deletingDuration,
            promotionsToKeep,
            includes,
            excludes,
        )
    }

    fun withDeletingDuration(deletingDuration: Int): StaleProperty {
        return if (this.deletingDuration == deletingDuration) this else StaleProperty(
            disablingDuration,
            deletingDuration,
            promotionsToKeep,
            includes,
            excludes,
        )
    }

    fun withPromotionsToKeep(promotionsToKeep: List<String>?): StaleProperty {
        return if (this.promotionsToKeep === promotionsToKeep) this else StaleProperty(
            disablingDuration,
            deletingDuration,
            promotionsToKeep,
            includes,
            excludes,
        )
    }

    companion object {
        fun create(): StaleProperty {
            return StaleProperty(0, 0, emptyList(), null, null)
        }
    }

}