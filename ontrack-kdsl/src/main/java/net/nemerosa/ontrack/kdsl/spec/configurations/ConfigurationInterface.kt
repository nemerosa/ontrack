package net.nemerosa.ontrack.kdsl.spec.configurations

import net.nemerosa.ontrack.json.parseInto
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import org.springframework.web.client.HttpClientErrorException.NotFound
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

    fun findByName(name: String): T? =
            try {
                connector.get("/extension/$id/configurations/$name").body.asJson().parseInto(type)
            } catch (ex: NotFound) {
                null
            }

}
