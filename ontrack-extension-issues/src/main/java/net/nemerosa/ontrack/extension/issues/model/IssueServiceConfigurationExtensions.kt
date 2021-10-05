package net.nemerosa.ontrack.extension.issues.model

fun IssueServiceConfiguration.toIdentifier() = IssueServiceConfigurationIdentifier(
    serviceId,
    name,
)
