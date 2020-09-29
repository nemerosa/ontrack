package net.nemerosa.ontrack.extension.stale

data class StaleProperty(
        val disablingDuration: Int,
        val deletingDuration: Int,
        val promotionsToKeep: List<String>?
) {

    fun withDisablingDuration(disablingDuration: Int): StaleProperty {
        return if (this.disablingDuration == disablingDuration) this else StaleProperty(disablingDuration, deletingDuration, promotionsToKeep)
    }

    fun withDeletingDuration(deletingDuration: Int): StaleProperty {
        return if (this.deletingDuration == deletingDuration) this else StaleProperty(disablingDuration, deletingDuration, promotionsToKeep)
    }

    fun withPromotionsToKeep(promotionsToKeep: List<String>?): StaleProperty {
        return if (this.promotionsToKeep === promotionsToKeep) this else StaleProperty(disablingDuration, deletingDuration, promotionsToKeep)
    }

    companion object {
        fun create(): StaleProperty {
            return StaleProperty(0, 0, emptyList())
        }
    }

}