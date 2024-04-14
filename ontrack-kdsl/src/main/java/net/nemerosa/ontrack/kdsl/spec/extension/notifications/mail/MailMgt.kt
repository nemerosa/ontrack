package net.nemerosa.ontrack.kdsl.spec.extension.notifications.mail

import net.nemerosa.ontrack.json.parse
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector

class MailMgt(connector: Connector) : Connected(connector) {

    fun findMailBy(to: String? = null, subject: String? = null): MockMail? {
        val query = mutableMapOf<String, String>()
        if (!to.isNullOrBlank()) {
            query["to"] = to
        }
        if (!subject.isNullOrBlank()) {
            query["subject"] = subject
        }
        return connector.get(
            path = "/extension/notifications/mail/mock/find",
            query = query,
        ).body.asJsonOrNull()?.parse()
    }

}