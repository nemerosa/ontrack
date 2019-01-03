package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.model.labels.Label
import net.nemerosa.ontrack.model.labels.LabelManagementService
import net.nemerosa.ontrack.model.labels.LabelProviderService
import net.nemerosa.ontrack.model.labels.description
import net.nemerosa.ontrack.repository.LabelRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LabelManagementServiceImpl(
        private val labelRepository: LabelRepository,
        private val labelProviderService: LabelProviderService
) : LabelManagementService {

    override val labels: List<Label>
        get() = labelRepository.labels.map {
            Label(
                    id = it.id,
                    category = it.category,
                    name = it.name,
                    description = it.description,
                    color = it.color,
                    computedBy = it.computedBy
                            ?.let { id -> labelProviderService.getLabelProvider(id) }
                            ?.description
            )
        }
}
