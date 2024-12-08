package net.nemerosa.ontrack.kdsl.spec.extension.casc

import net.nemerosa.ontrack.kdsl.spec.Ontrack

/**
 * Management of Casc.
 */
val Ontrack.casc: CascMgt get() = CascMgt(connector)
