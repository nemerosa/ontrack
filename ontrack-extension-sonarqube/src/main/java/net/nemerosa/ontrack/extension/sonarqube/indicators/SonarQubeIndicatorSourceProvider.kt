package net.nemerosa.ontrack.extension.sonarqube.indicators

import net.nemerosa.ontrack.extension.indicators.model.IndicatorSourceProvider
import org.springframework.stereotype.Component

@Component
class SonarQubeIndicatorSourceProvider : IndicatorSourceProvider {

    override val name: String = "SonarQube"

}