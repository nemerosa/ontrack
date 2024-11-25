package net.nemerosa.ontrack.extension.workflows

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.convert.DurationUnit
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.temporal.ChronoUnit

@Component
@ConfigurationProperties(prefix = "ontrack.config.extension.workflows")
class WorkflowConfigurationProperties {

    var mode: WorkflowMode = WorkflowMode.LEGACY

    @DurationUnit(ChronoUnit.SECONDS)
    var parentWaitingInterval: Duration = Duration.ofSeconds(1)

}

enum class WorkflowMode {
    LEGACY,
    PARALLEL,
}