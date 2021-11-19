package net.nemerosa.ontrack.kdsl.connector

import net.nemerosa.ontrack.json.parse

inline fun <reified T : Any> ConnectorResponseBody.parse(): T = asJson().parse()
