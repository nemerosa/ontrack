package net.nemerosa.ontrack.kdsl.spec.extension.av

import net.nemerosa.ontrack.kdsl.spec.Build

/**
 * Launches an auto versioning check for this build
 */
fun Build.autoVersioningCheck() {
    connector.post(
        "/extension/auto-versioning/build/$id/check"
    )
}