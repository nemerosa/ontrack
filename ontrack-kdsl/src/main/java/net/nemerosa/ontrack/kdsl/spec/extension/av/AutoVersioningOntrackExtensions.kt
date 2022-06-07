package net.nemerosa.ontrack.kdsl.spec.extension.av

import net.nemerosa.ontrack.kdsl.spec.Ontrack

/**
 * Management of auto versioning in Ontrack.
 */
val Ontrack.autoVersioning: AutoVersioningMgt get() = AutoVersioningMgt(connector)
