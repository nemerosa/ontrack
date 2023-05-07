package net.nemerosa.ontrack.kdsl.spec.configurations

import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import kotlin.reflect.KClass

class ConfigurationInterface<T : Any>(
    connector: Connector,
    val id: String,
    val type: KClass<T>,
) : Connected(connector) {

    fun create(conf: T) = connector.post(
        "/extension/$id/configurations/create",
        body = conf
    )

    fun delete(name: String) {
        connector.delete("/extension/$id/configurations/$name")
    }

}
