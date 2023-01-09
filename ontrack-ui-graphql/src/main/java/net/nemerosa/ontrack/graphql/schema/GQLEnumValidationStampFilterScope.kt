package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.graphql.support.getTypeDescription
import net.nemerosa.ontrack.model.structure.ValidationStampFilterScope
import org.springframework.stereotype.Component

@Component
class GQLEnumValidationStampFilterScope: AbstractGQLEnum<ValidationStampFilterScope>(
        ValidationStampFilterScope::class,
        ValidationStampFilterScope.values(),
        getTypeDescription(ValidationStampFilterScope::class)
)