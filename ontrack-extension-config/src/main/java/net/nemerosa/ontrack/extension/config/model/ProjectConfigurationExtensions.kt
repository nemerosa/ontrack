package net.nemerosa.ontrack.extension.config.model

fun ProjectConfiguration.getActualIssueServiceIdentifier(env: Map<String, String>): ProjectIssueServiceIdentifier? =
    issueServiceIdentifier
        ?: env.getEnv(
            EnvConstants.YONTRACK_CI_SCM_ISSUES,
            EnvConstants.YONTRACK_LEGACY_SCM_ISSUES,
        )
            ?.let { ProjectIssueServiceIdentifier.parse(it) }