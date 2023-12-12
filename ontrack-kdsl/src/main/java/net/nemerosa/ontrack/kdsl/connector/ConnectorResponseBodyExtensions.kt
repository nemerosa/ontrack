package net.nemerosa.ontrack.kdsl.connector

import net.nemerosa.ontrack.json.parse

inline fun <reified T : Any> ConnectorResponseBody.parse(): T = asJson().parse()

inline fun <reified T : Any> ConnectorResponseBody.parseOrNull(): T? = asJsonOrNull()?.parse()
