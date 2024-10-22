package net.nemerosa.ontrack.extensions.environments.ui

import net.nemerosa.ontrack.extension.api.DecorationExtension
import net.nemerosa.ontrack.extension.support.AbstractExtension
import net.nemerosa.ontrack.extensions.environments.EnvironmentsExtensionFeature
import net.nemerosa.ontrack.extensions.environments.SlotPipelineStub
import net.nemerosa.ontrack.extensions.environments.service.SlotService
import net.nemerosa.ontrack.model.structure.Build
import net.nemerosa.ontrack.model.structure.Decoration
import net.nemerosa.ontrack.model.structure.ProjectEntity
import net.nemerosa.ontrack.model.structure.ProjectEntityType
import org.springframework.stereotype.Component
import java.util.*

@Component
class BuildEnvironmentsDecorations(
    extensionFeature: EnvironmentsExtensionFeature,
    private val slotService: SlotService,
) : AbstractExtension(extensionFeature), DecorationExtension<List<SlotPipelineStub>> {

    override fun getDecorations(entity: ProjectEntity): List<Decoration<List<SlotPipelineStub>>> =
        if (entity is Build) {
            val stubs: List<SlotPipelineStub> = slotService.findSlotPipelineStubsByBuild(entity)
            listOf(
                Decoration.of(
                    this,
                    stubs
                )
            )
        } else {
            emptyList()
        }

    override fun getScope(): EnumSet<ProjectEntityType> = EnumSet.of(ProjectEntityType.BUILD)
}