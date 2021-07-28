package net.nemerosa.ontrack.extension.jenkins.indicator

import net.nemerosa.ontrack.extension.indicators.model.IndicatorSourceProvider
import org.springframework.stereotype.Component

@Component
class JenkinsPipelineLibraryIndicatorSourceProvider: IndicatorSourceProvider {

    override val name: String = "Jenkins pipeline library"

}