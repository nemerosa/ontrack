package net.nemerosa.ontrack.extension.environments.workflows

import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumSlotWorkflowTrigger : AbstractGQLEnum<SlotWorkflowTrigger>(
    type = SlotWorkflowTrigger::class,
    values = SlotWorkflowTrigger.values(),
    description = "List of triggers for the slot workflows",
)