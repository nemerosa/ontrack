package net.nemerosa.ontrack.boot

import net.nemerosa.ontrack.extension.support.AbstractExtensionFeature

class CoreExtensionFeature : AbstractExtensionFeature(
        "core",
        "Core",
        "Ontrack Core"
) {
    companion object {
        val INSTANCE = CoreExtensionFeature()
    }
}
