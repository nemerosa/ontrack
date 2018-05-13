package net.nemerosa.ontrack.service

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import net.nemerosa.ontrack.model.Ack
import net.nemerosa.ontrack.model.security.ProjectEdit
import net.nemerosa.ontrack.model.security.ProjectView
import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.*
import net.nemerosa.ontrack.repository.RunInfoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
@Transactional
class RunInfoServiceImpl(
        private val runInfoRepository: RunInfoRepository,
        private val structureService: StructureService,
        private val securityService: SecurityService,
        private val meterRegistry: MeterRegistry
) : RunInfoService {

    override fun getRunnableEntity(runnableEntityType: RunnableEntityType, id: Int): RunnableEntity {
        return runnableEntityType.load(structureService, id)
    }

    override fun getRunInfo(entity: RunnableEntity): RunInfo? {
        securityService.checkProjectFunction(entity, ProjectView::class.java)
        return runInfoRepository.getRunInfo(
                entity.runnableEntityType,
                entity.id()
        )
    }

    override fun setRunInfo(entity: RunnableEntity, input: RunInfoInput): RunInfo {
        securityService.checkProjectFunction(entity, entity.runnableEntityType.projectFunction.java)
        val runInfo = runInfoRepository.setRunInfo(
                entity.runnableEntityType,
                entity.id(),
                input,
                securityService.currentSignature
        )
        val time = input.runTime
        if (time != null) {
            meterRegistry.timer(
                    "ontrack_run_${entity.runnableEntityType.name}_time_seconds",
                    entity.runMetricTags.map { (name, value) -> Tag.of(name, value) }
            ).record(
                    time.toLong(),
                    TimeUnit.SECONDS
            )
        }
        return runInfo
    }

    override fun deleteRunInfo(runnableEntity: RunnableEntity): Ack {
        securityService.checkProjectFunction(runnableEntity, ProjectEdit::class.java)
        return runInfoRepository.deleteRunInfo(
                runnableEntity.runnableEntityType,
                runnableEntity.id()
        )
    }
}
