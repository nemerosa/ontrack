package net.nemerosa.ontrack.extension.indicators

import net.nemerosa.ontrack.model.support.OntrackConfigProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated

/**
 * Static configuration for the management of indicators
 */
@Component
@ConfigurationProperties(prefix = "${OntrackConfigProperties.PREFIX}.extension.indicators")
@Validated
class IndicatorConfigProperties {

    /**
     * Import configuration
     */
    var importing = ImportConfig()

    /**
     * Import configuration
     */
    class ImportConfig {
        /**
         * When a category/type does not exist any longer for a given import ID, must it be deleted?
         */
        var deleting: Boolean = false
    }

}
