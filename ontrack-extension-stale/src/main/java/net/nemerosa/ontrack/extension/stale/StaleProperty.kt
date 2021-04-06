package net.nemerosa.ontrack.extension.stale

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
    val disablingDuration: Int,
    val deletingDuration: Int?,
    val promotionsToKeep: List<String>?,
    val includes: String?,
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