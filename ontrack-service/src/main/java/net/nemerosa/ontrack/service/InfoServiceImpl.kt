package net.nemerosa.ontrack.service

import net.nemerosa.ontrack.extension.api.ExtensionManager
import net.nemerosa.ontrack.model.structure.Info
import net.nemerosa.ontrack.model.structure.InfoService
import net.nemerosa.ontrack.model.support.EnvService
import org.springframework.stereotype.Service

@Service
class InfoServiceImpl(
        private val envService: EnvService,
        private val extensionManager: ExtensionManager
) : InfoService {

    override val info: Info
        get() = Info(
                envService.version,
                extensionManager.extensionList
        )
}
