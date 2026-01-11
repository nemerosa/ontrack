package net.nemerosa.ontrack.extension.scm.changelog

import net.nemerosa.ontrack.model.events.EventRenderer

fun renderChangeLogIssues(
    renderer: EventRenderer,
    changeLog: SCMChangeLog
) = renderer.renderList(
    changeLog.issues?.issues?.map { issue ->
        val link = renderer.renderLink(
            text = issue.displayKey,
            href = issue.url,
        )
        val text = issue.summary
        "$link $text"
    } ?: emptyList()
)