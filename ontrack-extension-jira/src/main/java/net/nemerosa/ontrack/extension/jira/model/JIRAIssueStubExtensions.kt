package net.nemerosa.ontrack.extension.jira.model

fun JIRAIssue.toStub() = JIRAIssueStub(
    key = key,
    url = url,
)