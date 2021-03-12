package net.nemerosa.ontrack.extension.issues.model

import net.nemerosa.ontrack.model.exceptions.NotFoundException

class IssueServiceConfigurationIdentifierNotFoundException(value: String) :
    NotFoundException("Cannot find the issue service configuration for identifier: %s", value)
