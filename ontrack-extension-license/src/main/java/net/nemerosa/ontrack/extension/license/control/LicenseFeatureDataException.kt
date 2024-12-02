package net.nemerosa.ontrack.extension.license.control

import net.nemerosa.ontrack.common.BaseException

class LicenseFeatureDataException(featureID: String, message: String) : BaseException(
    """License issue for $featureID: $message"""
)
