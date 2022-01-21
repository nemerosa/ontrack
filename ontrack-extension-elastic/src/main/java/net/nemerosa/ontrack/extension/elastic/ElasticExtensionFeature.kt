package net.nemerosa.ontrack.extension.elastic

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import org.springframework.stereotype.Component

@Component
class ElasticExtensionFeature : AbstractExtensionFeature(
        "elastic",
        "Elastic",
        "Support for Elastic beyond search"
)
