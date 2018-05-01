package net.nemerosa.ontrack.extension.general

import net.nemerosa.ontrack.common.asOptional
import net.nemerosa.ontrack.extension.api.EntityInformationExtension
import net.nemerosa.ontrack.extension.api.model.EntityInformation
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.RunInfoService
import net.nemerosa.ontrack.model.structure.RunnableEntity
import org.springframework.stereotype.Component
import java.util.*

@Component
class RunInfoInformationExtension(
        extensionFeature: GeneralExtensionFeature,
        private val runInfoService: RunInfoService
) : AbstractExtension(extensionFeature), EntityInformationExtension {

    override fun getInformation(entity: ProjectEntity): Optional<EntityInformation> {
        if (entity is RunnableEntity) {
            // Gets the run info if any
            val runInfo = runInfoService.getRunInfo(entity)
            // Returns it as entity information data
            return runInfo?.run {
                EntityInformation(
                        this@RunInfoInformationExtension,
                        this
                )
            }.asOptional()
        } else {
            return Optional.empty()
        }
    }

}