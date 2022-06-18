package net.nemerosa.ontrack.extension.av.event

import net.nemerosa.ontrack.common.getOrNull
import net.nemerosa.ontrack.extension.av.dispatcher.AutoVersioningOrder
import net.nemerosa.ontrack.extension.scm.service.SCMPullRequest
import net.nemerosa.ontrack.model.events.Event
import net.nemerosa.ontrack.model.events.EventPostService
import net.nemerosa.ontrack.model.exceptions.ProjectNotFoundException
import net.nemerosa.ontrack.model.structure.Project
import net.nemerosa.ontrack.model.structure.StructureService
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.stereotype.Service

@Service
class AutoVersioningEventServiceImpl(
    private val eventPostService: EventPostService,
    private val structureService: StructureService,
) : AutoVersioningEventService {

    override fun sendError(order: AutoVersioningOrder, message: String, error: Exception?, pr: SCMPullRequest?) {
        eventPostService.post(
            error(
                order,
                message,
                error,
                pr
            )
        )
    }

    override fun sendSuccess(order: AutoVersioningOrder, message: String, pr: SCMPullRequest) {
        eventPostService.post(
            success(
                order,
                message,
                pr
            )
        )
    }

    private fun success(
        order: AutoVersioningOrder,
        message: String,
        pr: SCMPullRequest,
    ): Event =
        Event.of(AutoVersioningEvent.AUTO_VERSIONING_SUCCESS)
            .withRef(order.branch)
            .withProject(sourceProject(order))
            .with("message", message)
            .with("pr-name", pr.name)
            .with("pr-link", pr.link)
            .get()

    private fun error(
        order: AutoVersioningOrder,
        message: String,
        error: Exception?,
        pr: SCMPullRequest?,
    ): Event =
        Event.of(AutoVersioningEvent.AUTO_VERSIONING_ERROR)
            .withRef(order.branch)
            .withProject(sourceProject(order))
            .with("message", message)
            .run {
                if (pr != null) {
                    this.with("pr-name", pr.name)
                        .with("pr-link", pr.link)
                } else {
                    this
                }
            }
            .run {
                if (error != null) {
                    with("error-message", error.message)
                    with("error-stack", ExceptionUtils.getStackTrace(error))
                } else {
                    this
                }
            }
            .get()

    private fun sourceProject(order: AutoVersioningOrder): Project =
        structureService.findProjectByName(order.sourceProject).getOrNull()
            ?: throw ProjectNotFoundException(order.sourceProject)
}