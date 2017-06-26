package net.nemerosa.ontrack.extension.test

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature
import net.nemerosa.ontrack.model.extension.ExtensionFeatureOptions
import org.springframework.stereotype.Component

@Component
class TestFeature :
        AbstractExtensionFeature(
                "test",
                "Test",
                "Test extension",
                ExtensionFeatureOptions.DEFAULT
                        .withGui(true)
        )
