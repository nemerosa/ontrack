package net.nemerosa.ontrack.extension.jenkins.indicator

import net.nemerosa.ontrack.common.Version
import net.nemerosa.ontrack.extension.indicators.model.IndicatorCompliance
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * The settings may define the following configuration for a given library:
 *
 * * is the version required?
 * * what is the latest supported version?
 * * what is the last deprecated version?
 * * what is the last unsupported version?
 *
 * Depending on the actual version being used and the `required` flag, the
 * status compliance will be computed as follows:
 *
 * * if the version is not set:
 *    * if the version is required --> 0% compliance
 *    * if the version is not required --> 100% compliance
 * * if the version is set:
 *    * version >= latest supported version (good) --> 100% compliance
 *    * version > latest deprecated version (not latest, but not deprecated) --> 66% compliance
 *    * version > latest unsupported version (deprecated, but still working) --> 33% compliance
 *    * version <= latest unsupported version (cannot work) --> 0% compliance
 *
 * @property library Name of the library
 * @property required If the library version is required
 * @property lastSupported Latest version supported
 * @property lastDeprecated Latest version deprecated
 * @property lastUnsupported Latest version unsupported
 */
data class JenkinsPipelineLibraryIndicatorLibrarySettings(
    @APIDescription("Name of the library")
    @APILabel("Library")
    val library: String,
    @APIDescription("Is the library required?")
    @APILabel("Required")
    val required: Boolean = false,
    @APIDescription("Last supported version")
    @APILabel("Last supported version")
    val lastSupported: String? = null,
    @APIDescription("Last deprecated version")
    @APILabel("Last deprecated version")
    val lastDeprecated: String? = null,
    @APIDescription("Last unsupported version")
    @APILabel("Last unsupported version")
    val lastUnsupported: String? = null,
) {
    fun compliance(version: Version?) = IndicatorCompliance(complianceAsInt(version))

    internal fun complianceAsInt(version: Version?): Int =
        if (version == null) {
            if (required) {
                0
            } else {
                NOT_APPLICABLE
            }
        } else {
            val versions = listOf(
                lastSupported,
                lastDeprecated,
                lastUnsupported
            )
                .mapNotNull { Version.parseVersion(it) }
                .sortedDescending()
            if (versions.isEmpty()) {
                NOT_APPLICABLE
            } else {
                val n = versions.size.toDouble()
                val i = versions.indexOfFirst { boundary ->
                    version >= boundary
                }.toDouble()
                if (i < 0) {
                    0
                } else {
                    floor(100.0 * (1.0 - i / n)).roundToInt()
                }
            }
        }

    companion object {
        private const val NOT_APPLICABLE = 100
    }
}