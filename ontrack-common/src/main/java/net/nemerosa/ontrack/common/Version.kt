package net.nemerosa.ontrack.common

import org.apache.commons.lang3.StringUtils

/**
 * Representation of a semantic version (without extensions, only digits).
 */
data class Version(
        val major: Int = 0,
        val minor: Int = 0,
        val patch: Int = 0
) : Comparable<Version> {

    val isValid: Boolean
        get() = major >= 0 && minor >= 0 && patch >= 0 && (major > 0 || minor > 0 || patch > 0)

    override fun compareTo(other: Version): Int =
            compareValuesBy(this, other,
                    { it.major },
                    { it.minor },
                    { it.patch }
            )

    override fun toString(): String = "$major.$minor.$patch"

    companion object {

        private val PATTERN = "^([\\d]+)(\\.([\\d]+)(\\.([\\d]+))?)?.*$".toRegex()

        val NONE = Version()

        fun parseVersion(value: String?): Version? {
            return if (value != null && value.isNotBlank()) {
                val matcher = PATTERN.matchEntire(value)
                return if (matcher != null) {
                    val majorValue = matcher.groupValues[1]
                    val minorValue = matcher.groupValues[3]
                    val patchValue = matcher.groupValues[5]
                    val major = parse(majorValue)
                    val minor = parse(minorValue)
                    val patch = parse(patchValue)
                    Version(
                            major,
                            minor,
                            patch
                    )
                } else {
                    null
                }
            } else {
                null
            }
        }

        private fun parse(value: String): Int = when {
            value.isBlank() -> 0
            StringUtils.isNumeric(value) -> Integer.parseInt(value, 10)
            else -> -1
        }
    }
}
