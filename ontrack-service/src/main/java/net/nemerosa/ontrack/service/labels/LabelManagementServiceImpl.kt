package net.nemerosa.ontrack.service.labels

import net.nemerosa.ontrack.model.labels.*
import net.nemerosa.ontrack.repository.LabelRecord
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
        get() = labelRepository.labels.map { it.toLabel() }

    override fun newLabel(form: LabelForm): Label =
            labelRepository.newLabel(form).toLabel()

    private fun LabelRecord.toLabel() =
            Label(
                    id = id,
                    category = category,
                    name = name,
                    description = description,
                    color = color,
                    computedBy = computedBy
                            ?.let { id -> labelProviderService.getLabelProvider(id) }
                            ?.description
            )

}
