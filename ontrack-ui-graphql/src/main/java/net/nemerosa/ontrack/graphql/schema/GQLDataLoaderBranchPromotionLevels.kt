package net.nemerosa.ontrack.graphql.schema

import net.nemerosa.ontrack.model.structure.ID
import net.nemerosa.ontrack.model.structure.PromotionLevel
import net.nemerosa.ontrack.model.structure.StructureService
import org.springframework.stereotype.Component

@Component
class GQLDataLoaderBranchPromotionLevels(
        private val structureService: StructureService,
) : AbstractGQLDataLoader<ID, List<PromotionLevel>>() {

    companion object {
        const val KEY = "BranchPromotionLevels"
    }

    override val key: String = KEY

    override fun loadKeys(keys: List<ID>): List<List<PromotionLevel>> =
            keys.map { key ->
                structureService.getPromotionLevelListForBranch(key)
            }

}