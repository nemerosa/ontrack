package net.nemerosa.ontrack.extension.jenkins.autoversioning

import net.nemerosa.ontrack.common.BaseException

class JenkinsPostProcessingSettingsNotFoundException : BaseException(
    "Jenkins Auto Versioning Processing settings have not been set and auto versioning post processing cannot be completed."
)
