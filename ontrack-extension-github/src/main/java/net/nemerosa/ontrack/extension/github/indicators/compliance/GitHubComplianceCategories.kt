package net.nemerosa.ontrack.extension.github.indicators.compliance

import net.nemerosa.ontrack.extension.indicators.computing.IndicatorComputedCategory

object GitHubComplianceCategories {

    val structure = IndicatorComputedCategory("github-compliance-structure", "GitHub repository structure")
    val settings = IndicatorComputedCategory("github-compliance-settings", "GitHub repository settings")

}