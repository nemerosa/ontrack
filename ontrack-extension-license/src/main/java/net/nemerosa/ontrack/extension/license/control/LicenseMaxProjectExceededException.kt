package net.nemerosa.ontrack.extension.license.control

import net.nemerosa.ontrack.extension.license.License
import net.nemerosa.ontrack.model.exceptions.InputException

class LicenseMaxProjectExceededException(license: License) : InputException(
    """The maximum number of projects - ${license.maxProjects} - set by the license "${license.name}", assigned to "${license.assignee} has been exceeded. No new project can be created any longer."""
)