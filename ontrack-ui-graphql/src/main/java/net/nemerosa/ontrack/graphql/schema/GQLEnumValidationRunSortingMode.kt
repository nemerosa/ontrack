package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.model.structure.ValidationRunSortingMode
import org.springframework.stereotype.Component

@Component
class GQLEnumValidationRunSortingMode : AbstractGQLEnum<ValidationRunSortingMode>(
    ValidationRunSortingMode::class,
    ValidationRunSortingMode.values(),
    "Defines how validation runs should be sorted"
)