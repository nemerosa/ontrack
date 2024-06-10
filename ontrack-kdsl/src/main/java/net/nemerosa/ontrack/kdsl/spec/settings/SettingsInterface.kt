package net.nemerosa.ontrack.kdsl.spec.settings

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.parseInto
import kotlin.reflect.KClass

class SettingsInterface<T : Any>(
    connector: Connector,
    val id: String,
    val type: KClass<T>,
) : Connected(connector) {

    fun get(): T {
        return connector.get("/rest/settings/$id")
            .body.parseInto(type)
    }

    fun set(value: T) {
        connector.put("/rest/settings/$id", body = value)
    }

    fun with(update: (T) -> T, code: () -> Unit) {
        val old = get()
        try {
            set(update(old))
            code()
        } finally {
            set(old)
        }
    }

}
