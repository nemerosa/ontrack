package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.model.labels.LabelProvider
import org.springframework.stereotype.Service

@Service
class NOPLabelProvider : LabelProvider {
    override val name: String = "NOP"
}
