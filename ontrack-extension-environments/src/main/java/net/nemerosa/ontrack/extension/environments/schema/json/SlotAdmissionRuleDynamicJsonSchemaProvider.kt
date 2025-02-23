package net.nemerosa.ontrack.extension.environments.schema.json

import net.nemerosa.ontrack.extension.environments.SlotAdmissionRule
import net.nemerosa.ontrack.model.annotations.getAPITypeDescription
import net.nemerosa.ontrack.model.json.schema.DynamicJsonSchemaProvider
import net.nemerosa.ontrack.model.json.schema.JsonType
import net.nemerosa.ontrack.model.json.schema.JsonTypeBuilder
import org.springframework.stereotype.Component
import kotlin.reflect.full.starProjectedType

@Component
class SlotAdmissionRuleDynamicJsonSchemaProvider(
    private val slotAdmissionRules: List<SlotAdmissionRule<*, *>>,
) : DynamicJsonSchemaProvider {

    override val discriminatorValues: List<String>
        get() = slotAdmissionRules.map { it.id }

    override fun getConfigurationTypes(builder: JsonTypeBuilder): Map<String, JsonType> =
        slotAdmissionRules.associate { rule ->
            rule.id to getConfigurationType(rule, builder)
        }

    private fun getConfigurationType(
        rule: SlotAdmissionRule<*, *>,
        builder: JsonTypeBuilder,
    ): JsonType {
        val configType = rule.configType
        return builder.toType(
            type = configType.starProjectedType,
            description = getAPITypeDescription(rule::class)
        )
    }

    override fun toRef(id: String): String = "slot-admission-rule-$id"

}