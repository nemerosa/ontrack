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

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JobType) return false

        if (category != other.category) return false
        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        var result = category.hashCode()
        result = 31 * result + key.hashCode()
        return result
    }


    companion object {
        @JvmStatic
        fun of(category: JobCategory, key: String): JobType {
            return JobType(category, key, key)
        }
    }

}
