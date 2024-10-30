package net.nemerosa.ontrack.extension.environments.ui

import net.nemerosa.ontrack.extension.environments.SlotAdmissionRuleInputFieldType
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumSlotAdmissionRuleInputFieldType : AbstractGQLEnum<SlotAdmissionRuleInputFieldType>(
    type = SlotAdmissionRuleInputFieldType::class,
    values = SlotAdmissionRuleInputFieldType.values(),
    description = "Type of field for an input",
)