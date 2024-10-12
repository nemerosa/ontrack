package net.nemerosa.ontrack.extension.license.control

import net.nemerosa.ontrack.common.BaseException

class LicenseFeatureException(featureID: String) : BaseException(
    """Feature not allowed by the license: $featureID"""
)
