package net.nemerosa.ontrack.extension.av.audit

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
class AutoVersioningAuditQueryFilter(
    val project: String? = null,
    val branch: String? = null,
    val uuid: String? = null,
    val state: AutoVersioningAuditState? = null,
    val running: Boolean? = null,
    val source: String? = null,
    val version: String? = null,
    val offset: Int = 0,
    val count: Int = 10
) {
    fun withOffset(value: Int) = AutoVersioningAuditQueryFilter(
        project = project,
        branch = branch,
        uuid = uuid,
        state = state,
        running = running,
        source = source,
        version = version,
        offset = value,
        count = count
    )
    fun withCount(value: Int) = AutoVersioningAuditQueryFilter(
        project = project,
        branch = branch,
        uuid = uuid,
        state = state,
        source = source,
        running = running,
        version = version,
        offset = offset,
        count = value
    )
}