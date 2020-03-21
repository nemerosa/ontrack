package net.nemerosa.ontrack.job

data class JobCategory(
        val key: String,
        val name: String
) {

    fun withName(name: String) = JobCategory(key, name)

    fun getType(key: String): JobType {
        return JobType.of(this, key)
    }



    override fun toString(): String {
        return String.format(
                "[%s]",
                key
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JobCategory) return false

        if (key != other.key) return false

        return true
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    companion object {

        /**
         * Core category, used internally
         */
        @JvmField
        val CORE = JobCategory.of("core").withName("Core")

        @JvmStatic
        fun of(key: String): JobCategory {
            return JobCategory(key, key)
        }
    }

}
