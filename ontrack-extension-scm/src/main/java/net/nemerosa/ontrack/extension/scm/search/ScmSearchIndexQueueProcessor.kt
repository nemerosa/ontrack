package net.nemerosa.ontrack.extension.scm.search

import net.nemerosa.ontrack.extension.queue.QueueMetadata
import net.nemerosa.ontrack.extension.queue.QueueProcessor
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KClass

@Component
class ScmSearchIndexQueueProcessor(
    private val scmSearchIndexService: ScmSearchIndexService,
    private val securityService: SecurityService,
    private val structureService: StructureService,
) : QueueProcessor<ScmSearchIndexQueueItem> {

    override val id: String = "scm-search-index"

    override val payloadType: KClass<ScmSearchIndexQueueItem> = ScmSearchIndexQueueItem::class

    override fun getRoutingIdentifier(payload: ScmSearchIndexQueueItem): String = payload.projectName

    override fun isCancelled(payload: ScmSearchIndexQueueItem): String? = null

    override val maxConcurrency: Int = 1

    override fun process(
        payload: ScmSearchIndexQueueItem,
        queueMetadata: QueueMetadata?,
    ) {
        securityService.asAdmin {
            val project = structureService.findProjectByName(payload.projectName).getOrNull()
            if (project != null) {
                scmSearchIndexService.index(project)
            } else {
                throw ScmSearchIndexProjectNotFoundException(payload.projectName)
            }
        }
    }
}