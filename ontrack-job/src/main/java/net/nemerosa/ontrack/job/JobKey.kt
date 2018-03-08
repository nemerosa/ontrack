package net.nemerosa.ontrack.job

data class JobKey(
        val type: JobType,
        val id: String
) {

    fun sameType(type: JobType): Boolean {
        return this.type == type
    }

    fun sameCategory(category: JobCategory): Boolean {
        return this.type.category == category
    }

    override fun toString(): String {
        return String.format(
                "%s[%s]",
                type,
                id
        )
    }

    companion object {
        @JvmStatic
        fun of(type: JobType, id: String): JobKey {
            return JobKey(type, id)
        }
    }
}
