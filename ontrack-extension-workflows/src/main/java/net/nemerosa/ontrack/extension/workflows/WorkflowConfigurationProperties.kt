package net.nemerosa.ontrack.extension.workflows

import net.nemerosa.ontrack.model.annotations.APIDescription
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DurationUnit
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit

@Component
@ConfigurationProperties(prefix = "ontrack.config.extension.workflows")
class WorkflowConfigurationProperties {

    @APIDescription("Time to wait for the completion of the parents of a node in a workflow")
    @DurationUnit(ChronoUnit.SECONDS)
    var parentWaitingInterval: Duration = Duration.ofSeconds(1)

}
