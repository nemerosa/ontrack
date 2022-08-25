package net.nemerosa.ontrack.extension.license.control

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.extension.license.License

class LicenseMaxProjectExceededException(license: License) : BaseException(
    """The maximum number of projects - ${license.maxProjects} - set by the license "${license.name}", assigned to "${license.assignee} has been exceeded. No new project can be created any longer."""
)