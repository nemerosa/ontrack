package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.repository.RunInfoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RunInfoServiceImpl(
        private val runInfoRepository: RunInfoRepository,
        private val structureService: StructureService,
        private val securityService: SecurityService
) : RunInfoService {

    override fun getRunnableEntity(runnableEntityType: RunnableEntityType, id: Int): RunnableEntity {
        return runnableEntityType.load(structureService, id)
    }

    override fun getRunInfo(entity: RunnableEntity): RunInfo? =
            runInfoRepository.getRunInfo(
                    entity.runnableEntityType,
                    entity.id()
            )

    override fun setRunInfo(entity: RunnableEntity, input: RunInfoInput): RunInfo =
            runInfoRepository.setRunInfo(
                    entity.runnableEntityType,
                    entity.id(),
                    input,
                    securityService.currentSignature
            )

    override fun deleteRunInfo(runnableEntity: RunnableEntity): Ack =
            runInfoRepository.deleteRunInfo(
                    runnableEntity.runnableEntityType,
                    runnableEntity.id()
            )
}
