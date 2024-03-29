package net.nemerosa.ontrack.extension.github.ingestion.processing.events

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.extension.github.ingestion.processing.AbstractIngestionEventProcessor
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventPreprocessingCheck
import net.nemerosa.ontrack.extension.github.ingestion.processing.IngestionEventProcessingResultDetails
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class PingIngestionEventProcessor : AbstractIngestionEventProcessor<PingPayload>() {

    /**
     * Not doing anything
     */
    override fun process(payload: PingPayload, configuration: String?): IngestionEventProcessingResultDetails =
        IngestionEventProcessingResultDetails.ignored("Ping events are not processed.")

    override val payloadType: KClass<PingPayload> = PingPayload::class

    override val event: String = "ping"

    /**
     * Pings are acknowledged but not processed.
     */
    override fun preProcessingCheck(payload: PingPayload): IngestionEventPreprocessingCheck =
        IngestionEventPreprocessingCheck.IGNORED

    override fun getPayloadSource(payload: PingPayload): String? = null
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class PingPayload(
    val zen: String,
)
