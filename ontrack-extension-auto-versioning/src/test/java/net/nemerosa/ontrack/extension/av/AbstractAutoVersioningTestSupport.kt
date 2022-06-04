package net.nemerosa.ontrack.extension.av

import net.nemerosa.ontrack.extension.av.config.AutoVersioningConfigurationService
import net.nemerosa.ontrack.graphql.AbstractQLKTITSupport
import org.springframework.beans.factory.annotation.Autowired

abstract class AbstractAutoVersioningTestSupport : AbstractQLKTITSupport() {

    @Autowired
    protected lateinit var autoVersioningConfigurationService: AutoVersioningConfigurationService

}