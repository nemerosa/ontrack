package net.nemerosa.ontrack.model.structure

import com.fasterxml.jackson.annotation.JsonProperty

class ValidationRunStatusID(
        val id: String,
        val name: String,
        @JsonProperty("root")
        val isRoot: Boolean,
        @JsonProperty("passed")
        val isPassed: Boolean,
        val followingStatuses: Collection<String> = emptyList()
) {

    companion object {
        const val DEFECTIVE = "DEFECTIVE"
        @JvmField
        val STATUS_DEFECTIVE = ValidationRunStatusID(DEFECTIVE, "Defective", isRoot = false, isPassed = false)
        const val EXPLAINED = "EXPLAINED"
        @JvmField
        val STATUS_EXPLAINED = ValidationRunStatusID(EXPLAINED, "Explained", false, isPassed = false)
        const val FAILED = "FAILED"
        @JvmField
        val STATUS_FAILED = ValidationRunStatusID(FAILED, "Failed", true, isPassed = false)
        const val FIXED = "FIXED"
        @JvmField
        val STATUS_FIXED = ValidationRunStatusID(FIXED, "Fixed", false, isPassed = true)
        const val INTERRUPTED = "INTERRUPTED"
        @JvmField
        val STATUS_INTERRUPTED = ValidationRunStatusID(INTERRUPTED, "Interrupted", isRoot = true, isPassed = false)
        const val INVESTIGATING = "INVESTIGATING"
        @JvmField
        val STATUS_INVESTIGATING = ValidationRunStatusID(INVESTIGATING, "Investigating", isRoot = true, isPassed = false)
        const val PASSED = "PASSED"
        @JvmField
        val STATUS_PASSED = ValidationRunStatusID(PASSED, "Passed", isRoot = true, isPassed = true)
        const val WARNING = "WARNING"
        @JvmField
        val STATUS_WARNING = ValidationRunStatusID(id = WARNING, name = "Warning", isRoot = true, isPassed = false)

        @JvmStatic
        fun of(id: String, name: String, root: Boolean, passed: Boolean): ValidationRunStatusID {
            return ValidationRunStatusID(id, name, root, passed, emptyList());
        }
    }

    fun addDependencies(vararg followingStatuses: String): ValidationRunStatusID {
        val dependencies = this.followingStatuses + followingStatuses
        return ValidationRunStatusID(id, name, isRoot, isPassed, dependencies)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ValidationRunStatusID) return false

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}
