package net.nemerosa.ontrack.extension.tfc.hook

import net.nemerosa.ontrack.common.BaseException

class TFCSettingsMissingTokenException : BaseException(
    """The TFC secret token for the TFC hook has not been set."""
)
