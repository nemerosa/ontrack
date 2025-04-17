package net.nemerosa.ontrack.kdsl.spec.configurations

import net.nemerosa.ontrack.json.asJson
import net.nemerosa.ontrack.json.parseInto
import net.nemerosa.ontrack.kdsl.connector.Connected
import net.nemerosa.ontrack.kdsl.connector.Connector
import net.nemerosa.ontrack.kdsl.connector.graphql.convert
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.CreateConfigurationMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.DeleteConfigurationMutation
import net.nemerosa.ontrack.kdsl.connector.graphql.schema.FindConfigurationByNameQuery
import net.nemerosa.ontrack.kdsl.connector.graphqlConnector
import net.nemerosa.ontrack.kdsl.spec.Configuration
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
        graphqlConnector.mutate(
            DeleteConfigurationMutation(
                type = id,
                name = name,
            )
        ) { it?.deleteConfiguration?.payloadUserErrors?.convert() }
    }

    fun findByName(name: String): T? =
        graphqlConnector.query(
            FindConfigurationByNameQuery(
                type = id,
                name = name
            )
        )?.configurationByName?.data?.parseInto(type)

}
