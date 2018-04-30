package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.security.SecurityService
import net.nemerosa.ontrack.model.structure.RunInfo
import net.nemerosa.ontrack.model.structure.RunInfoInput
import net.nemerosa.ontrack.model.structure.RunInfoService
import net.nemerosa.ontrack.model.structure.RunnableEntityType
import net.nemerosa.ontrack.repository.RunInfoRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RunInfoServiceImpl(
        private val runInfoRepository: RunInfoRepository,
        private val securityService: SecurityService
) : RunInfoService {

    override fun getRunInfo(runnableEntityType: RunnableEntityType, id: Int): RunInfo =
            runInfoRepository.getRunInfo(runnableEntityType, id)

    override fun setRunInfo(runnableEntityType: RunnableEntityType, id: Int, input: RunInfoInput): RunInfo =
            runInfoRepository.setRunInfo(runnableEntityType, id, input, securityService.currentSignature)
}
