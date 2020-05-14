package net.nemerosa.ontrack.extension.indicators.imports

import net.nemerosa.ontrack.extension.indicators.model.IndicatorSourceProvider
import org.springframework.stereotype.Component

@Component
class ImportsIndicatorSourceProvider : IndicatorSourceProvider {
    override val name: String = "Import"
}