package net.nemerosa.ontrack.extension.scm

import net.nemerosa.ontrack.model.annotations.APIDescription
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties(prefix = "ontrack.config.extension.scm")
@Component
class SCMExtensionConfigProperties {

    @APIDescription("SCM catalog properties")
    var catalog = SCMCatalogConfigProperties()

    class SCMCatalogConfigProperties {
        @APIDescription("Enabling the SCM catalog")
        var enabled = false
    }

}
