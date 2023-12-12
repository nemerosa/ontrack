package net.nemerosa.ontrack.kdsl.spec.extension.jenkins.mock

import net.nemerosa.ontrack.kdsl.spec.extension.jenkins.JenkinsMgt

val JenkinsMgt.mock: JenkinsMockMgt get() = JenkinsMockMgt(connector)
