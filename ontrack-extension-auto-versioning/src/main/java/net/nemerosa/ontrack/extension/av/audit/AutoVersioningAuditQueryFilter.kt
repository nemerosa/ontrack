package net.nemerosa.ontrack.extension.av.audit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.nemerosa.ontrack.model.annotations.API
import net.nemerosa.ontrack.model.annotations.APIDescription

@JsonIgnoreProperties(ignoreUnknown = true)
class AutoVersioningAuditQueryFilter(
    val project: String? = null,
    val branch: String? = null,
    val uuid: String? = null,
    val state: AutoVersioningAuditState? = null,
    val states: Set<AutoVersioningAuditState>? = null,
    val running: Boolean? = null,
    val source: String? = null,
    val version: String? = null,
    @APIDescription("Routing key used for the queuing")
    val routing: String? = null,
    @APIDescription("Actual queue the order was posted to")
    val queue: String? = null,
    val offset: Int = 0,
    val count: Int = 10,
    @APIDescription("Set of paths")
    val targetPaths: List<String>? = null,
) {
    fun withOffset(value: Int) = AutoVersioningAuditQueryFilter(
        project = project,
        branch = branch,
        uuid = uuid,
        state = state,
        states = states,
        running = running,
        source = source,
        version = version,
        routing = routing,
        queue = queue,
        offset = value,
        count = count,
        targetPaths = targetPaths,
    )
    fun withCount(value: Int) = AutoVersioningAuditQueryFilter(
        project = project,
        branch = branch,
        uuid = uuid,
        state = state,
        states = states,
        source = source,
        running = running,
        version = version,
        routing = routing,
        queue = queue,
        offset = offset,
        count = value,
        targetPaths = targetPaths,
    )
}