package net.nemerosa.ontrack.extension.sonarqube

import net.nemerosa.ontrack.extension.indicators.IndicatorsExtensionFeature
import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.job.JobCategory
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class SonarQubeExtensionFeature(
        indicatorsExtensionFeature: IndicatorsExtensionFeature
) : AbstractExtensionFeature(
        "sonarqube",
        "SonarQube",
        "Support for SonarQube metrics in Ontrack",
        ExtensionFeatureOptions.DEFAULT.withGui(true)
                .withDependency(indicatorsExtensionFeature)
) {
    companion object {
        val SONARQUBE_JOB_CATEGORY: JobCategory = JobCategory.of("sonarqube").withName("SonarQube")
    }
}