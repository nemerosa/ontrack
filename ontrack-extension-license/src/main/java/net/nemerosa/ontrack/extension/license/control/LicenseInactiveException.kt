package net.nemerosa.ontrack.extension.license.control

import net.nemerosa.ontrack.extension.license.License
import net.nemerosa.ontrack.model.exceptions.InputException

class LicenseInactiveException(license: License) : InputException(
    """License "${license.name}", assigned to "${license.assignee} is inactive."""
)