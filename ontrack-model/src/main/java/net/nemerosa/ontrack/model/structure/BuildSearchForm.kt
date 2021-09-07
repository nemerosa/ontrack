package net.nemerosa.ontrack.model.structure

import net.nemerosa.ontrack.model.annotations.APIDescription
import javax.validation.constraints.Min
import javax.validation.constraints.Size

data class BuildSearchForm(
    @Min(1)
    @APIDescription("Maximum number of builds to return.")
    val maximumCount: Int = 10,
    @APIDescription("Regular expression to match against the branch name.")
    val branchName: String? = null,
    @APIDescription("Regular expression to match against the build name, unless `buildExactMatch` is set to `true`.")
    val buildName: String? = null,
    @APIDescription("Matches a build having at least this promotion.")
    val promotionName: String? = null,
    @APIDescription("Matches a build having at least this validation with PASSED as a status.")
    val validationStampName: String? = null,
    @APIDescription("Matches a build having this property.")
    val property: String? = null,
    @Size(max = 200)
    @APIDescription("When `property` is set, matches against the property value.")
    val propertyValue: String? = null,
    @APIDescription("When `buildName` is set, considers an exact match on the build name.")
    val buildExactMatch: Boolean = false,
    @APIDescription("`project:build` expression, matches against builds being linked from the build to match.")
    val linkedFrom: String? = null,
    @APIDescription("`project:build` expression, matches against builds being linked to the build to match.")
    val linkedTo: String? = null,
)
