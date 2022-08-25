package net.nemerosa.ontrack.extension.license.control

import net.nemerosa.ontrack.common.BaseException
import net.nemerosa.ontrack.extension.license.License

class LicenseExpiredException(license: License) : BaseException(
    """License "${license.name}", assigned to "${license.assignee} expired on ${license.validUntil}."""
)