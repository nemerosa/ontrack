package net.nemerosa.ontrack.extension.environments.ui

import net.nemerosa.ontrack.extension.environments.SlotPipelineChangeType
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumSlotPipelineChangeType : AbstractGQLEnum<SlotPipelineChangeType>(
    type = SlotPipelineChangeType::class,
    values = SlotPipelineChangeType.values(),
    description = "Type of change in a deployment",
)