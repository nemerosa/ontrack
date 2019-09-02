package net.nemerosa.ontrack.extension.sonarqube

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions

class SonarQubeExtensionFeature : AbstractExtensionFeature(
        "sonarqube",
        "SonarQube",
        "Support for SonarQube metrics in Ontrack",
        ExtensionFeatureOptions.DEFAULT.withGui(true)
)
