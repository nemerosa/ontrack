package net.nemerosa.ontrack.model.preferences

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.model.annotations.APIDescription

/**
 * Representation for the preferences of a user.
 *
 * @property branchViewLegacy Old generation of the branch view
 * @property branchViewVsNames Displaying the names of the validation stamps
 * @property branchViewVsGroups Grouping validations per status
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@APIDescription("Preferences of a user")
data class Preferences(
    @APIDescription("Branch view legacy")
    var branchViewLegacy: Boolean = true,
    @APIDescription("Branch view VS names")
    var branchViewVsNames: Boolean = true,
    @APIDescription("Branch view VS groups")
    var branchViewVsGroups: Boolean = true,
)
