package net.nemerosa.ontrack.kdsl.spec.extension.jira

import net.nemerosa.ontrack.kdsl.spec.Ontrack

/**
 * Management of Jira in Ontrack.
 */
val Ontrack.jira: JiraMgt get() = JiraMgt(connector)
