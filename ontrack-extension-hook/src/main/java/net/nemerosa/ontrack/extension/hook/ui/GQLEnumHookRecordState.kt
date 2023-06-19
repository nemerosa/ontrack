package net.nemerosa.ontrack.extension.hook.ui

import net.nemerosa.ontrack.extension.hook.records.HookRecordState
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import org.springframework.stereotype.Component

@Component
class GQLEnumHookRecordState : AbstractGQLEnum<HookRecordState>(
        type = HookRecordState::class,
        values = HookRecordState.values(),
        description = getTypeDescription(HookRecordState::class),
)