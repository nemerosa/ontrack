package net.nemerosa.ontrack.extension.queue

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = QueueConfigProperties.PREFIX)
class QueueConfigProperties {

    /**
     * General properties
     */
    var general = GeneralProperties()

    /**
     * Specific properties
     */
    val specific = mutableMapOf<String, SpecificProperties>()

    /**
     * Processing properties
     */
    abstract class ProcessingProperties {
        var async: Boolean = true
    }

    /**
     * General properties
     */
    class GeneralProperties: ProcessingProperties() {
        var warnIfAsync: Boolean = true
    }

    /**
     * Specific properties
     */
    class SpecificProperties : ProcessingProperties()

    companion object {
        const val PREFIX = "ontrack.extension.queue"
    }
}