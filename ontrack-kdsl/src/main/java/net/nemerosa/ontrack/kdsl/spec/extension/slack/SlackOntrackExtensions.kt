package net.nemerosa.ontrack.kdsl.spec.extension.slack

import net.nemerosa.ontrack.kdsl.spec.Ontrack

/**
 * Management of notifications in Ontrack.
 */
val Ontrack.slack: SlackMgt get() = SlackMgt(connector)
