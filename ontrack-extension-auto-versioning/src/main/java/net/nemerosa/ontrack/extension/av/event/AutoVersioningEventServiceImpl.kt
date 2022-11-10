package net.nemerosa.ontrack.extension.av.event

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.av.event.AutoVersioningEvents.AUTO_VERSIONING_ERROR
import net.nemerosa.ontrack.extension.av.event.AutoVersioningEvents.AUTO_VERSIONING_PR_MERGE_TIMEOUT_ERROR
import net.nemerosa.ontrack.extension.av.event.AutoVersioningEvents.AUTO_VERSIONING_SUCCESS
import net.nemerosa.ontrack.extension.scm.service.SCMPullRequest
import net.nemerosa.ontrack.model.events.*
import net.nemerosa.ontrack.model.exceptions.ProjectNotFoundException
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.support.StartupService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AutoVersioningEventServiceImpl(
    private val structureService: StructureService,
    private val eventFactory: EventFactory,
    private val eventPostService: EventPostService,
) : AutoVersioningEventService, StartupService {

    /**
     * We do need a separate transaction to send events in case of error
     * because the current transaction WILL be cancelled
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun sendError(order: AutoVersioningOrder, message: String, error: Exception) {
        eventPostService.post(
            error(order, message, error)
        )
    }

    override fun sendPRMergeTimeoutError(order: AutoVersioningOrder, pr: SCMPullRequest) {
        eventPostService.post(
            prMergeTimeoutError(order, pr)
        )
    }

    override fun sendSuccess(order: AutoVersioningOrder, message: String, pr: SCMPullRequest) {
        eventPostService.post(
            success(order, message, pr)
        )
    }

    override fun getName(): String = "Registration of auto versioning events"

    override fun startupOrder(): Int = StartupService.JOB_REGISTRATION

    override fun start() {
        eventFactory.register(AUTO_VERSIONING_SUCCESS)
        eventFactory.register(AUTO_VERSIONING_ERROR)
        eventFactory.register(AUTO_VERSIONING_PR_MERGE_TIMEOUT_ERROR)
    }

    internal fun success(
        order: AutoVersioningOrder,
        message: String,
        pr: SCMPullRequest,
    ): Event =
        Event.of(AUTO_VERSIONING_SUCCESS)
            .withBranch(order.branch)
            .withExtraProject(sourceProject(order))
            .with("version", order.targetVersion)
            .with("message", close(message))
            .with("pr-name", pr.name)
            .with("pr-link", pr.link)
            .build()

    internal fun error(
        order: AutoVersioningOrder,
        message: String,
        error: Exception,
    ): Event =
        Event.of(AUTO_VERSIONING_ERROR)
            .withBranch(order.branch)
            .withExtraProject(sourceProject(order))
            .with("version", order.targetVersion)
            .with("message", close(message))
            .with("error", close(error.message ?: error::class.java.name))
            .build()

    internal fun prMergeTimeoutError(
        order: AutoVersioningOrder,
        pr: SCMPullRequest,
    ): Event =
        Event.of(AUTO_VERSIONING_PR_MERGE_TIMEOUT_ERROR)
            .withBranch(order.branch)
            .withExtraProject(sourceProject(order))
            .with("version", order.targetVersion)
            .with("pr-name", pr.name)
            .with("pr-link", pr.link)
            .build()

    private fun sourceProject(order: AutoVersioningOrder) =
        structureService.findProjectByName(order.sourceProject)
            .getOrNull()
            ?: throw ProjectNotFoundException(order.sourceProject)

    private fun close(message: String) = if (message.endsWith(".")) {
        message
    } else {
        "$message."
    }

}