package net.nemerosa.ontrack.extension.sonarqube.property

import net.nemerosa.ontrack.extension.sonarqube.SonarQubeMeasuresList
import net.nemerosa.ontrack.extension.sonarqube.configuration.SonarQubeConfiguration
import net.nemerosa.ontrack.model.annotations.APIDescription
import net.nemerosa.ontrack.model.annotations.APILabel
import net.nemerosa.ontrack.model.docs.DocumentationType
import net.nemerosa.ontrack.model.support.ConfigurationProperty
import java.net.URLEncoder

class SonarQubeProperty(
    @DocumentationType("String", "Name of the SonarQube configuration")
    override val configuration: SonarQubeConfiguration,
    val key: String,
    val validationStamp: String,
    override val measures: List<String>,
    val override: Boolean,
    val branchModel: Boolean,
    val branchPattern: String?,
    @APIDescription("If checked, collected SQ measures will be attached as metrics to the validation.")
    @APILabel("Metrics")
    val validationMetrics: Boolean,
) : ConfigurationProperty<SonarQubeConfiguration>, SonarQubeMeasuresList {

    val projectUrl: String
        get() = "${configuration.url}/dashboard?id=${URLEncoder.encode(key, "UTF-8")}"

    companion object {
        const val DEFAULT_VALIDATION_STAMP = "sonarqube"
    }
}
