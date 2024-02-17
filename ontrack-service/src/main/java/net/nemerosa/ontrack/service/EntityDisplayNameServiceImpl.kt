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
            is Branch -> branchDisplayNameService.getBranchDisplayName(entity)
            is Build -> buildDisplayNameService.getBuildDisplayName(entity)
            else -> entity.defaultDisplayName
        }
}