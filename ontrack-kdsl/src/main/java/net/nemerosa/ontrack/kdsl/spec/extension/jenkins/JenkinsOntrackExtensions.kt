package net.nemerosa.ontrack.kdsl.spec.extension.jenkins

import net.nemerosa.ontrack.kdsl.spec.Ontrack

/**
 * Management of Jenkins in Ontrack.
 */
val Ontrack.jenkins: JenkinsMgt get() = JenkinsMgt(connector)
