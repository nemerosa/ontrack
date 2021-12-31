package net.nemerosa.ontrack.model.preferences

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Representation for the preferences of a user.
 *
 * @property branchViewLegacy Old generation of the branch view
 * @property branchViewVsNames Displaying the names of the validation stamps
 * @property branchViewVsGroups Grouping validations per status
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Preferences(
    var branchViewLegacy: Boolean = true,
    var branchViewVsNames: Boolean = true,
    var branchViewVsGroups: Boolean = true,
)
