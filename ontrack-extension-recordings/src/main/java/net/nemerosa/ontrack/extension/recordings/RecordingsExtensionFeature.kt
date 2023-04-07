package net.nemerosa.ontrack.extension.recordings

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class RecordingsExtensionFeature : AbstractExtensionFeature(
        "recordings",
        "Recordings",
        "Used by extensions which need to record messages",
        ExtensionFeatureOptions.DEFAULT
)
