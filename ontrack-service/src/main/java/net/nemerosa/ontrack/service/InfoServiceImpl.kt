package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.model.structure.Info
import net.nemerosa.ontrack.model.structure.InfoService
import net.nemerosa.ontrack.model.support.EnvService
import org.springframework.stereotype.Service

@Service
class InfoServiceImpl(
        private val envService: EnvService
) : InfoService {

    override val info: Info
        get() = Info(
                envService.version
        )
}
