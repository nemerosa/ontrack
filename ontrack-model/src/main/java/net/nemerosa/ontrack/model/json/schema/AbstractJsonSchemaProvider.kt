package net.nemerosa.ontrack.model.json.schema

import net.nemerosa.ontrack.model.support.EnvService

abstract class AbstractJsonSchemaProvider(
    private val envService: EnvService,
) : JsonSchemaProvider {

    override val id: String
        get() =
            "https://yontrack.com/refs/schemas/${envService.version.display}/${key}"
}