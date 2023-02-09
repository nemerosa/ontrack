package net.nemerosa.ontrack.extension.github.ingestion.ui

import net.nemerosa.ontrack.extension.github.ingestion.config.model.IngestionConfigVSNameNormalization
import net.nemerosa.ontrack.graphql.schema.AbstractGQLEnum
import org.springframework.stereotype.Component

@Component
class GQLEnumIngestionConfigVSNameNormalization : AbstractGQLEnum<IngestionConfigVSNameNormalization>(
    type = IngestionConfigVSNameNormalization::class,
    values = IngestionConfigVSNameNormalization.values(),
    description = "Defines the way a computed name must be normalized before it can be used as a validation stamp name."
)
