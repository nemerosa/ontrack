package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.model.labels.LabelForm
import net.nemerosa.ontrack.model.labels.LabelProvider
import net.nemerosa.ontrack.model.structure.Project
import org.springframework.stereotype.Service

@Service
class NOPLabelProvider : LabelProvider {
    override val name: String = "NOP"

    override fun getLabelsForProject(project: Project): List<LabelForm> = emptyList()
}
