package net.nemerosa.ontrack.extension.license.control

import net.nemerosa.ontrack.extension.license.License
import net.nemerosa.ontrack.model.exceptions.InputException

class LicenseExpiredException(license: License) : InputException(
    """License "${license.name}", assigned to "${license.assignee} expired on ${license.validUntil}."""
)