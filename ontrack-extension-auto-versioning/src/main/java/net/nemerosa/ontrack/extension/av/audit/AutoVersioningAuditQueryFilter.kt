package net.nemerosa.ontrack.extension.av.audit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
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
)
