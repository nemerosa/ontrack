package net.nemerosa.ontrack.kdsl.spec.extension.slack.mock

import net.nemerosa.ontrack.kdsl.spec.extension.slack.SlackMgt

val SlackMgt.mock: MockSlackMgt get() = MockSlackMgt(connector)
