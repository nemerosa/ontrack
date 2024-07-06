package net.nemerosa.ontrack.kdsl.spec.extension.jira.mock

import net.nemerosa.ontrack.kdsl.spec.extension.jira.JiraMgt

val JiraMgt.mock: JiraMockMgt get() = JiraMockMgt(connector)
