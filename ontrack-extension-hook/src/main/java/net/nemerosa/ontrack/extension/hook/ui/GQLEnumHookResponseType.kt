package net.nemerosa.ontrack.extension.hook.ui

import net.nemerosa.ontrack.extension.hook.HookResponseType
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import net.nemerosa.ontrack.graphql.support.getTypeDescription
import org.springframework.stereotype.Component

@Component
class GQLEnumHookResponseType : AbstractGQLEnum<HookResponseType>(
        type = HookResponseType::class,
        values = HookResponseType.values(),
        description = getTypeDescription(HookResponseType::class),
)