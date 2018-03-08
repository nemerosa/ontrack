package net.nemerosa.ontrack.job

data class JobType(
        val category: JobCategory,
        val key: String,
        val name: String
) {

    fun withName(name: String) = JobType(category, key, name)

    fun getKey(id: String): JobKey {
        return JobKey.of(this, id)
    }

    override fun toString(): String {
        return String.format(
                "%s[%s]",
                category,
                key
        )
    }

    companion object {
        @JvmStatic
        fun of(category: JobCategory, key: String): JobType {
            return JobType(category, key, key)
        }
    }

}
