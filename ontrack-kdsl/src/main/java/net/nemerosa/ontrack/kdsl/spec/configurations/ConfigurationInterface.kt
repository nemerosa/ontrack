package net.nemerosa.ontrack.kdsl.spec.configurations

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseInto
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.CreateConfigurationMutation
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.spec.Configuration
import org.springframework.web.client.HttpClientErrorException.NotFound
import kotlin.reflect.KClass

class ConfigurationInterface<T : Configuration>(
    connector: Connector,
    val id: String,
    val type: KClass<T>,
) : Connected(connector) {

    fun create(conf: T) = graphqlConnector.mutate(
        CreateConfigurationMutation(
            name = conf.name,
            type = id,
            data = conf.asJson()
        )
    ) { it?.createConfiguration?.payloadUserErrors?.convert() }

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
