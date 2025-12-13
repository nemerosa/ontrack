package net.nemerosa.ontrack.extension.api.support

import net.nemerosa.ontrack.model.json.schema.JsonSchemaString
import net.nemerosa.ontrack.model.support.ConfigurationProperty

class TestProperty(
    @JsonSchemaString
    override val configuration: TestConfiguration,
    val value: String
) : ConfigurationProperty<TestConfiguration> {

    companion object {
        @JvmStatic
        fun of(value: String) = of(
            TestConfiguration.config("test"),
            value
        )

        @JvmStatic
        fun of(configuration: TestConfiguration, value: String) = TestProperty(
            configuration,
            value
        )
    }
}