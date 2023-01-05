package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.StructureService
import net.nemerosa.ontrack.model.structure.ValidationStamp
import org.springframework.stereotype.Component

@Component
class GQLDataLoaderBranchValidationStamps(
        private val structureService: StructureService,
) : AbstractGQLDataLoader<ID, List<ValidationStamp>>() {

    companion object {
        const val KEY = "BranchValidationStamps"
    }

    override fun loadKeys(keys: List<ID>): List<List<ValidationStamp>> =
            keys.map { id ->
                structureService.getValidationStampListForBranch(id)
            }

    override val key: String = KEY
}