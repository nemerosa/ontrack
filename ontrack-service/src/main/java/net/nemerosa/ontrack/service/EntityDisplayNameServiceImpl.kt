package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.structure.*
import org.springframework.stereotype.Service

@Service
class EntityDisplayNameServiceImpl(
    private val branchDisplayNameService: BranchDisplayNameService,
    private val buildDisplayNameService: BuildDisplayNameService,
) : EntityDisplayNameService {

    override fun getEntityDisplayName(entity: ProjectEntity): String =
        when (entity) {
            is Branch -> branchDisplayNameService.getBranchDisplayName(entity, BranchNamePolicy.DISPLAY_NAME_OR_NAME)
            is Build -> buildDisplayNameService.getBuildDisplayNameOrName(entity)
            else -> entity.defaultDisplayName
        }
}