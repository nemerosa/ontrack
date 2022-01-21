package net.nemerosa.ontrack.extension.elastic.metrics

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

/**
 * See https://www.elastic.co/guide/en/ecs/current/index.html
 */
data class ECSEntry(
    @JsonProperty("@timestamp")
    val timestamp: LocalDateTime,
    val event: ECSEvent,
    val labels: Map<String, String>? = null,
    val tags: List<String>? = null,
    /**
     * Specific entries for Ontrack (non-ECS)
     */
    @JsonProperty("Ontrack")
    val ontrack: Map<String, Any?>? = null,
)

data class ECSEvent(
    val kind: ECSEventKind = ECSEventKind.metric,
    val category: String,
    val type: String? = null,
    val outcome: ECSEventOutcome? = null,
    val action: String? = null,
    val dataset: String? = null,
    val duration: Long? = null,
    val module: String? = null,
)

enum class ECSEventKind {
    metric,
}

enum class ECSEventOutcome {
    failure,
    success,
    unknown,
}
