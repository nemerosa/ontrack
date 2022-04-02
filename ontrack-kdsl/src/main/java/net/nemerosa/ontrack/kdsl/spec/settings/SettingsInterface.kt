package net.nemerosa.ontrack.kdsl.spec.settings

import net.nemerosa.ontrack.json.parseInto
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import kotlin.reflect.KClass

class SettingsInterface<T : Any>(
    connector: Connector,
    val id: String,
    val type: KClass<T>,
) : Connected(connector) {

    fun get(): T = connector.get("/rest/settings/$id")
        .body.asJson().parseInto(type)

    fun set(value: T) {
        connector.put("/rest/settings/$id", body = value)
    }

}
